package team.floracore.common.storage.implementation.sql;

import com.github.benmanes.caffeine.cache.*;
import com.google.gson.reflect.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.implementation.sql.connection.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.io.*;
import java.lang.reflect.*;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

public class SqlStorage implements StorageImplementation {
    private static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {
    }.getType();
    private final FloraCorePlugin plugin;
    private final ConnectionFactory connectionFactory;
    private final Function<String, String> statementProcessor;
    AsyncCache<UUID, Players> playersCache = Caffeine.newBuilder().expireAfterWrite(5, TimeUnit.SECONDS).maximumSize(10000).executor(Executors.newSingleThreadExecutor()).buildAsync();

    public SqlStorage(FloraCorePlugin plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor().compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public FloraCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    public Function<String, String> getStatementProcessor() {
        return this.statementProcessor;
    }

    @Override
    public void init() throws Exception {
        this.connectionFactory.init(this.plugin);
        applySchema();
    }

    private void applySchema() throws IOException, SQLException {
        List<String> statements;

        String schemaFileName = "team/floracore/schema/" + this.connectionFactory.getImplementationName().toLowerCase(Locale.ROOT) + ".sql";
        try (InputStream is = this.plugin.getBootstrap().getResourceStream(schemaFileName)) {
            if (is == null) {
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getImplementationName());
            }

            statements = SchemaReader.getStatements(is).stream().map(this.statementProcessor).collect(Collectors.toList());
        }

        try (Connection connection = this.connectionFactory.getConnection()) {
            boolean utf8mb4Unsupported = false;

            try (Statement s = connection.createStatement()) {
                for (String query : statements) {
                    s.addBatch(query);
                }

                try {
                    s.executeBatch();
                } catch (BatchUpdateException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        utf8mb4Unsupported = true;
                    } else {
                        throw e;
                    }
                }
            }

            // try again
            if (utf8mb4Unsupported) {
                try (Statement s = connection.createStatement()) {
                    for (String query : statements) {
                        s.addBatch(query.replace("utf8mb4", "utf8"));
                    }

                    s.executeBatch();
                }
            }
        }
    }

    @Override
    public void shutdown() {
        try {
            this.connectionFactory.shutdown();
        } catch (Exception e) {
            this.plugin.getLogger().severe("Exception whilst disabling SQL storage", e);
        }
    }

    public Players selectPlayerBaseInfo(UUID uuid) {
        CompletableFuture<Players> p = playersCache.get(uuid, u -> {
            Players players;
            try (Connection c = this.connectionFactory.getConnection()) {
                try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Players.SELECT))) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("name");
                            String firstLoginIp = rs.getString("firstLoginIp");
                            String lastLoginIp = rs.getString("lastLoginIp");
                            Date firstLoginTime = rs.getDate("firstLoginTime");
                            Date lastLoginTime = rs.getDate("lastLoginTime");
                            long playTime = rs.getLong("playTime");
                            players = new Players(plugin, this, u, name, firstLoginIp, lastLoginIp, firstLoginTime, lastLoginTime, playTime);
                        } else {
                            Player player = Bukkit.getPlayer(u);
                            if (player != null) {
                                String name = player.getName();
                                String loginIp = Objects.requireNonNull(player.getAddress()).getHostString();
                                players = new Players(plugin, this, u, name, loginIp);
                                players.init();
                            }
                            throw new RuntimeException("指定用户不存在！");
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return players;
        });
        return p.join();
    }

    public void deletePlayers(UUID u) throws SQLException {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Players.DELETE))) {
                ps.setString(1, u.toString());
                ps.execute();
            }
        }
    }
}
