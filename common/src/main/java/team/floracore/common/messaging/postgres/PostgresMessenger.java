package team.floracore.common.messaging.postgres;

import com.impossibl.postgres.api.jdbc.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.*;
import org.floracore.api.messenger.message.*;
import team.floracore.common.plugin.*;
import team.floracore.common.plugin.scheduler.*;
import team.floracore.common.storage.implementation.sql.*;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * An implementation of {@link Messenger} using Postgres.
 */
public class PostgresMessenger implements Messenger {
    private static final String CHANNEL = "floracore:update";

    private final FloraCorePlugin plugin;
    private final SqlStorage sqlStorage;
    private final IncomingMessageConsumer consumer;

    private NotificationListener listener;
    private SchedulerTask checkConnectionTask;

    public PostgresMessenger(FloraCorePlugin plugin, SqlStorage sqlStorage, IncomingMessageConsumer consumer) {
        this.plugin = plugin;
        this.sqlStorage = sqlStorage;
        this.consumer = consumer;
    }

    public void init() {
        checkAndReopenConnection(true);
        this.checkConnectionTask = this.plugin.getBootstrap().getScheduler().asyncRepeating(() -> checkAndReopenConnection(false), 5, TimeUnit.SECONDS);
    }

    @Override
    public void sendOutgoingMessage(@NonNull OutgoingMessage outgoingMessage) {
        try (PGConnection connection = this.sqlStorage.getConnectionFactory().getConnection().unwrap(PGConnection.class)) {
            try (PreparedStatement ps = connection.prepareStatement("SELECT pg_notify(?, ?)")) {
                ps.setString(1, CHANNEL);
                ps.setString(2, outgoingMessage.asEncodedString());
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            this.checkConnectionTask.cancel();
            if (this.listener != null) {
                this.listener.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks the connection, and re-opens it if necessary.
     *
     * @return true if the connection is now alive, false otherwise
     */
    private boolean checkAndReopenConnection(boolean firstStartup) {
        boolean listenerActive = this.listener != null && this.listener.isListening();
        if (listenerActive) {
            return true;
        }

        // (re)create

        if (!firstStartup) {
            this.plugin.getLogger().warn("Postgres listen/notify connection dropped, trying to re-open the connection");
        }

        try {
            this.listener = new NotificationListener();
            this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
                this.listener.listenAndBind();
                if (!firstStartup) {
                    this.plugin.getLogger().info("Postgres listen/notify connection re-established");
                }
            });
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private class NotificationListener implements PGNotificationListener, AutoCloseable {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final AtomicBoolean listening = new AtomicBoolean(false);

        public void listenAndBind() {
            try (PGConnection connection = PostgresMessenger.this.sqlStorage.getConnectionFactory().getConnection().unwrap(PGConnection.class)) {
                connection.addNotificationListener(CHANNEL, this);

                try (Statement s = connection.createStatement()) {
                    s.execute("LISTEN \"" + CHANNEL + "\"");
                }

                this.listening.set(true);
                this.latch.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.listening.set(false);
            }
        }

        public boolean isListening() {
            return this.listening.get();
        }

        @Override
        public void notification(int processId, String channelName, String payload) {
            if (!CHANNEL.equals(channelName)) {
                return;
            }
            PostgresMessenger.this.consumer.consumeIncomingMessageAsString(payload);
        }

        @Override
        public void closed() {
            this.latch.countDown();
        }

        @Override
        public void close() {
            this.latch.countDown();
        }
    }

}
