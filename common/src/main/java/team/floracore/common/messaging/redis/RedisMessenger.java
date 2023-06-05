package team.floracore.common.messaging.redis;

import org.floracore.api.messenger.IncomingMessageConsumer;
import org.floracore.api.messenger.Messenger;
import org.floracore.api.messenger.message.OutgoingMessage;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.*;
import team.floracore.common.plugin.FloraCorePlugin;

/**
 * An implementation of {@link Messenger} using Redis.
 */
public class RedisMessenger implements Messenger {
    private static final String CHANNEL = "floracore:messenger";

    private final FloraCorePlugin plugin;
    private final IncomingMessageConsumer consumer;

    private /* final */ JedisPool jedisPool;
    private /* final */ Subscription sub;
    private boolean closing = false;

    public RedisMessenger(FloraCorePlugin plugin, IncomingMessageConsumer consumer) {
        this.plugin = plugin;
        this.consumer = consumer;
    }

    public void init(String address, String username, String password, boolean ssl) {
        String[] addressSplit = address.split(":");
        String host = addressSplit[0];
        int port = addressSplit.length > 1 ? Integer.parseInt(addressSplit[1]) : Protocol.DEFAULT_PORT;

        if (username == null) {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), host, port, Protocol.DEFAULT_TIMEOUT, password, ssl);
        } else {
            this.jedisPool = new JedisPool(new JedisPoolConfig(),
                    host,
                    port,
                    Protocol.DEFAULT_TIMEOUT,
                    username,
                    password,
                    ssl);
        }

        this.sub = new Subscription();
        this.plugin.getBootstrap().getScheduler().executeAsync(this.sub);
    }

    @Override
    public void sendOutgoingMessage(@NotNull OutgoingMessage outgoingMessage) {
        try (Jedis jedis = this.jedisPool.getResource()) {
            jedis.publish(CHANNEL, outgoingMessage.asEncodedString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        this.closing = true;
        this.sub.unsubscribe();
        this.jedisPool.destroy();
    }

    private class Subscription extends JedisPubSub implements Runnable {
        @Override
        public void run() {
            boolean first = true;
            while (!RedisMessenger.this.closing && !Thread.interrupted() && !RedisMessenger.this.jedisPool.isClosed()) {
                try (Jedis jedis = RedisMessenger.this.jedisPool.getResource()) {
                    if (first) {
                        first = false;
                    } else {
                        RedisMessenger.this.plugin.getLogger().info("Redis pubsub connection re-established");
                    }
                    jedis.subscribe(this, CHANNEL); // blocking call
                } catch (Exception e) {
                    if (RedisMessenger.this.closing) {
                        return;
                    }
                    RedisMessenger.this.plugin.getLogger()
                            .warn("Redis pubsub connection dropped, trying to re-open the " +
                                            "connection",
                                    e);
                    try {
                        unsubscribe();
                    } catch (Exception ignored) {

                    }
                    // Sleep for 5 seconds to prevent massive spam in console
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        @Override
        public void onMessage(String channel, String msg) {
            if (!channel.equals(CHANNEL)) {
                return;
            }
            RedisMessenger.this.consumer.consumeIncomingMessageAsString(msg);
        }
    }

}
