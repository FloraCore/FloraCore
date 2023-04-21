package team.floracore.common.storage.implementation.sql;

import com.github.benmanes.caffeine.cache.*;
import com.google.gson.reflect.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.implementation.sql.connection.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.io.*;
import java.lang.reflect.*;
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
    AsyncCache<UUID, List<Data>> dataCache = Caffeine.newBuilder().expireAfterWrite(3, TimeUnit.SECONDS).maximumSize(10000).executor(Executors.newSingleThreadExecutor()).buildAsync();

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


    @Override
    public Players selectPlayers(UUID uuid, String n, String loginIp) {
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
                            long firstLoginTime = rs.getLong("firstLoginTime");
                            long lastLoginTime = rs.getLong("lastLoginTime");
                            long playTime = rs.getLong("playTime");
                            players = new Players(plugin, this, u, name, firstLoginIp, lastLoginIp, firstLoginTime, lastLoginTime, playTime);
                        } else {
                            players = new Players(plugin, this, u, n, loginIp);
                            players.init();
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

    @Override
    public Players selectPlayers(UUID uuid) {
        return selectPlayers(uuid, null, null);
    }


    @Override
    public void deletePlayers(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Players.DELETE))) {
                ps.setString(1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Data insertData(UUID uuid, String type, String key, String value, long expiry) {
        Data data = new Data(plugin, this, uuid, type, key, value, expiry);
        try {
            data.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public List<Data> selectData(UUID uuid) {
        CompletableFuture<List<Data>> d = dataCache.get(uuid, u -> {
            List<Data> ret = new ArrayList<>();
            try (Connection c = this.connectionFactory.getConnection()) {
                try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.SELECT))) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String type = rs.getString("type");
                            String key = rs.getString("key");
                            String value = rs.getString("value");
                            long expiry = rs.getLong("expiry");
                            ret.add(new Data(plugin, this, u, type, key, value, expiry));
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return ret;
        });
        return d.join();
    }

    @Override
    public Data getSpecifiedData(UUID uuid, String type, String key) {
        List<Data> ret = selectData(uuid);
        for (Data data : ret) {
            if (data.getType().equalsIgnoreCase(type) && data.getKey().equalsIgnoreCase(key)) {
                long currentTime = System.currentTimeMillis(); // 获取当前时间戳
                if (data.getExpiry() <= 0 || data.getExpiry() > currentTime) {
                    return data;
                }
            }
        }
        return null;
    }

    @Override
    public void deleteDataAll(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.DELETE_ALL))) {
                ps.setString(1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDataExpired(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.DELETE_EXPIRED))) {
                ps.setString(1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDataType(UUID uuid, String type) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.DELETE_TYPE))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, type);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDataKey(UUID uuid, String type, String key) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.DELETE_KEY))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, type);
                ps.setString(3, key);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
