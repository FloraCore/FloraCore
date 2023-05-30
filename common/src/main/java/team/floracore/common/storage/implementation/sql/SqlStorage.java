package team.floracore.common.storage.implementation.sql;

import com.google.gson.reflect.*;
import org.floracore.api.data.*;
import org.floracore.api.server.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.implementation.*;
import team.floracore.common.storage.implementation.sql.connection.*;
import team.floracore.common.storage.misc.floracore.tables.*;
import team.floracore.common.util.gson.*;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class SqlStorage implements StorageImplementation {
    private static final Type LIST_STRING_TYPE = new TypeToken<List<String>>() {
    }.getType();
    private final FloraCorePlugin plugin;
    private final ConnectionFactory connectionFactory;
    private final Function<String, String> statementProcessor;

    public SqlStorage(FloraCorePlugin plugin, ConnectionFactory connectionFactory, String tablePrefix) {
        this.plugin = plugin;
        this.connectionFactory = connectionFactory;
        this.statementProcessor = connectionFactory.getStatementProcessor()
                .compose(s -> s.replace("{prefix}", tablePrefix));
    }

    @Override
    public FloraCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public String getImplementationName() {
        return this.connectionFactory.getImplementationName();
    }

    @Override
    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    @Override
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

        String schemaFileName = "team/floracore/schema/" + this.connectionFactory.getImplementationName()
                .toLowerCase(Locale.ROOT) + ".sql";
        try (InputStream is = this.plugin.getBootstrap().getResourceStream(schemaFileName)) {
            if (is == null) {
                throw new IOException("Couldn't locate schema file for " + this.connectionFactory.getImplementationName());
            }

            statements = SchemaReader.getStatements(is)
                    .stream()
                    .map(this.statementProcessor)
                    .collect(Collectors.toList());
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
    public PLAYER selectPlayer(String name) {
        PLAYER player;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(PLAYER.SELECT_NAME))) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String uuid = rs.getString("uuid");
                        String firstLoginIp = rs.getString("firstLoginIp");
                        String lastLoginIp = rs.getString("lastLoginIp");
                        long firstLoginTime = rs.getLong("firstLoginTime");
                        long lastLoginTime = rs.getLong("lastLoginTime");
                        long playTime = rs.getLong("playTime");
                        player = new PLAYER(plugin,
                                this,
                                id,
                                UUID.fromString(uuid),
                                name,
                                firstLoginIp,
                                lastLoginIp,
                                firstLoginTime,
                                lastLoginTime,
                                playTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return player;
    }

    @Override
    public PLAYER selectPlayer(UUID uuid) {
        PLAYER player;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(PLAYER.SELECT))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String firstLoginIp = rs.getString("firstLoginIp");
                        String lastLoginIp = rs.getString("lastLoginIp");
                        long firstLoginTime = rs.getLong("firstLoginTime");
                        long lastLoginTime = rs.getLong("lastLoginTime");
                        long playTime = rs.getLong("playTime");
                        player = new PLAYER(plugin,
                                this,
                                id,
                                uuid,
                                name,
                                firstLoginIp,
                                lastLoginIp,
                                firstLoginTime,
                                lastLoginTime,
                                playTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return player;
    }

    @Override
    public void deletePlayer(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(PLAYER.DELETE))) {
                ps.setString(1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DATA insertData(UUID uuid, DataType type, String key, String value, long expiry) {
        DATA data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            data = new DATA(plugin, this, -1, uuid, type, key, value, expiry);
            try {
                data.init();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            data.setValue(value);
            data.setExpiry(expiry);
        }
        return data;
    }

    @Override
    public List<DATA> selectData(UUID uuid) {
        List<DATA> ret = new ArrayList<>();
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(DATA.SELECT))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String type = rs.getString("type");
                        String key = rs.getString("data_key");
                        String value = rs.getString("value");
                        long expiry = rs.getLong("expiry");
                        ret.add(new DATA(plugin, this, id, uuid, DataType.parse(type), key, value, expiry));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public DATA getSpecifiedData(UUID uuid, DataType type, String key) {
        List<DATA> ret = selectData(uuid);
        for (DATA data : ret) {
            if (data.getType() == type && data.getKey().equalsIgnoreCase(key)) {
                long currentTime = System.currentTimeMillis();
                if (data.getExpiry() <= 0 || data.getExpiry() > currentTime) {
                    return data;
                }
            }
        }
        return null;
    }

    @Override
    public List<DATA> getSpecifiedTypeData(UUID uuid, DataType type) {
        List<DATA> ret = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (DATA data : selectData(uuid)) {
            if (data.getType() == type && (data.getExpiry() <= 0 || data.getExpiry() > currentTime)) {
                ret.add(data);
            }
        }
        return ret;
    }

    @Override
    public void deleteDataAll(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(DATA.DELETE_ALL))) {
                ps.setString(1, uuid.toString());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDataType(UUID uuid, DataType type) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(DATA.DELETE_TYPE))) {
                ps.setString(1, uuid.toString());
                ps.setString(2, type.getName());
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDataExpired(UUID uuid) {
        List<DATA> ret = selectData(uuid);
        for (DATA data : ret) {
            long currentTime = System.currentTimeMillis();
            if (!(data.getExpiry() <= 0 || data.getExpiry() > currentTime)) {
                deleteDataID(data.getId());
            }
        }
    }

    @Override
    public void deleteDataID(int id) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(DATA.DELETE_ID))) {
                ps.setInt(1, id);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SERVER selectServer(String name) {
        SERVER server;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(SERVER.SELECT))) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String type = rs.getString("type");
                        boolean autoSync1 = rs.getBoolean("autoSync1");
                        boolean autoSync2 = rs.getBoolean("autoSync2");
                        long lastActiveTime = rs.getLong("lastActiveTime");
                        server = new SERVER(plugin,
                                this,
                                id,
                                name,
                                ServerType.parse(type),
                                autoSync1,
                                autoSync2,
                                lastActiveTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return server;
    }

    @Override
    public PARTY selectParty(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(PARTY.SELECT_UUID))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        UUID leader = UUID.fromString(rs.getString("leader"));
                        String moderatorsJson = rs.getString("moderators");
                        Type type1 = new TypeToken<List<UUID>>() {
                        }.getType();
                        List<UUID> moderators = GsonProvider.normal().fromJson(moderatorsJson, type1);
                        String membersJson = rs.getString("members");
                        Type type2 = new TypeToken<List<UUID>>() {
                        }.getType();
                        List<UUID> members = GsonProvider.normal().fromJson(membersJson, type2);
                        String settings = rs.getString("settings");
                        long createTime = rs.getLong("createTime");
                        long disbandTime = rs.getLong("disbandTime");
                        return new PARTY(plugin,
                                this,
                                id,
                                uuid,
                                leader,
                                moderators,
                                members,
                                settings,
                                createTime,
                                disbandTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PARTY selectEffectiveParty(UUID uuid) {
        PARTY party = selectParty(uuid);
        if (party.getDisbandTime() > 0) {
            return null;
        } else {
            return party;
        }
    }

    @Override
    public void insertParty(UUID uuid, UUID leader, long createTime) {
        PARTY party = new PARTY(plugin,
                this,
                -1,
                uuid,
                leader,
                Collections.emptyList(),
                Collections.singletonList(leader),
                "",
                createTime,
                -1);
        try {
            party.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ONLINE selectOnline(UUID uuid) {
        ONLINE online;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(ONLINE.SELECT))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        boolean status = rs.getBoolean("status");
                        String serverName = rs.getString("serverName");
                        online = new ONLINE(plugin, this, uuid, status, serverName);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return online;
    }

    @Override
    public void insertOnline(UUID uuid, boolean status, String serverName) {
        ONLINE online = new ONLINE(plugin, this, uuid, status, serverName);
        try {
            online.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
