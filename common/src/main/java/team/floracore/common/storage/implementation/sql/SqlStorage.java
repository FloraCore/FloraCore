package team.floracore.common.storage.implementation.sql;

import com.google.gson.reflect.*;
import org.floracore.api.commands.report.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
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
    public Players selectPlayers(UUID uuid) {
        Players players;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Players.SELECT))) {
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
                        players = new Players(plugin, this, id, uuid, name, firstLoginIp, lastLoginIp, firstLoginTime, lastLoginTime, playTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
    }

    @Override
    public Players selectPlayers(String name) {
        Players players;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Players.SELECT_NAME))) {
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
                        players = new Players(plugin, this, id, UUID.fromString(uuid), name, firstLoginIp, lastLoginIp, firstLoginTime, lastLoginTime, playTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return players;
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
    public Data insertData(UUID uuid, DataType type, String key, String value, long expiry) {
        Data data = getSpecifiedData(uuid, type, key);
        if (data == null) {
            data = new Data(plugin, this, -1, uuid, type, key, value, expiry);
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
    public List<Data> selectData(UUID uuid) {
        List<Data> ret = new ArrayList<>();
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.SELECT))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String type = rs.getString("type");
                        String key = rs.getString("data_key");
                        String value = rs.getString("value");
                        long expiry = rs.getLong("expiry");
                        ret.add(new Data(plugin, this, id, uuid, DataType.parse(type), key, value, expiry));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public Data getSpecifiedData(UUID uuid, DataType type, String key) {
        List<Data> ret = selectData(uuid);
        for (Data data : ret) {
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
    public List<Data> getSpecifiedTypeData(UUID uuid, DataType type) {
        List<Data> ret = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        for (Data data : selectData(uuid)) {
            if (data.getType() == type && (data.getExpiry() <= 0 || data.getExpiry() > currentTime)) {
                ret.add(data);
            }
        }
        return ret;
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
    public void deleteDataType(UUID uuid, DataType type) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.DELETE_TYPE))) {
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
        List<Data> ret = selectData(uuid);
        for (Data data : ret) {
            long currentTime = System.currentTimeMillis();
            if (!(data.getExpiry() <= 0 || data.getExpiry() > currentTime)) {
                deleteDataID(data.getId());
            }
        }
    }

    @Override
    public void deleteDataID(int id) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Data.DELETE_ID))) {
                ps.setInt(1, id);
                ps.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Servers selectServers(String name) {
        Servers servers;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Servers.SELECT))) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String type = rs.getString("type");
                        boolean autoSync1 = rs.getBoolean("autoSync1");
                        boolean autoSync2 = rs.getBoolean("autoSync2");
                        long lastActiveTime = rs.getLong("lastActiveTime");
                        servers = new Servers(plugin, this, id, name, ServerType.parse(type), autoSync1, autoSync2, lastActiveTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return servers;
    }

    @Override
    public List<Chat> selectChat(String name) {
        List<Chat> ret = new ArrayList<>();
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Chat.SELECT))) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String recordsJson = rs.getString("records");
                        Type type = new TypeToken<List<ChatRecord>>() {
                        }.getType();
                        List<ChatRecord> records = GsonProvider.normal().fromJson(recordsJson, type);
                        long startTime = rs.getLong("startTime");
                        long endTime = rs.getLong("endTime");
                        ret.add(new Chat(plugin, this, id, name, records, startTime, endTime));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public Chat selectChatWithStartTime(String name, long startTime) {
        Chat chat;
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Chat.SELECT_WITH_START_TIME))) {
                ps.setString(1, name);
                ps.setLong(2, startTime);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String recordsJson = rs.getString("records");
                        Type type = new TypeToken<List<ChatRecord>>() {
                        }.getType();
                        List<ChatRecord> records = GsonProvider.normal().fromJson(recordsJson, type);
                        long endTime = rs.getLong("endTime");
                        chat = new Chat(plugin, this, id, name, records, startTime, endTime);
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return chat;
    }

    @Override
    public void insertChat(String name, long startTime) {
        Chat chat = new Chat(plugin, this, name, startTime);
        try {
            chat.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Report> getReports() {
        List<Report> ret = new ArrayList<>();
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Report.SELECT))) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        UUID reporter = UUID.fromString(rs.getString("reporter"));
                        UUID reported = UUID.fromString(rs.getString("reported"));
                        String reason = rs.getString("reported");
                        long reportTime = rs.getLong("reportTime");
                        UUID handler = UUID.fromString(rs.getString("handler"));
                        Long handleTime = rs.getLong("handleTime");
                        Boolean conclusion = rs.getBoolean("conclusion");
                        Long conclusionTime = rs.getLong("conclusionTime");
                        String recordsJson = rs.getString("chat");
                        Type type = new TypeToken<List<ReportDataChatRecord>>() {
                        }.getType();
                        List<ReportDataChatRecord> records = GsonProvider.normal().fromJson(recordsJson, type);
                        ret.add(new Report(plugin, this, id, uuid, reporter, reported, reason, reportTime, handler, handleTime, conclusion, conclusionTime, records));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public Report selectReport(UUID uuid) {
        try (Connection c = this.connectionFactory.getConnection()) {
            try (PreparedStatement ps = c.prepareStatement(this.statementProcessor.apply(Report.SELECT_UUID))) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        UUID reporter = UUID.fromString(rs.getString("reporter"));
                        UUID reported = UUID.fromString(rs.getString("reported"));
                        String reason = rs.getString("reported");
                        long reportTime = rs.getLong("reportTime");
                        UUID handler = UUID.fromString(rs.getString("handler"));
                        Long handleTime = rs.getLong("handleTime");
                        Boolean conclusion = rs.getBoolean("conclusion");
                        Long conclusionTime = rs.getLong("conclusionTime");
                        String recordsJson = rs.getString("chat");
                        Type type = new TypeToken<List<ReportDataChatRecord>>() {
                        }.getType();
                        List<ReportDataChatRecord> records = GsonProvider.normal().fromJson(recordsJson, type);
                        return new Report(plugin, this, id, uuid, reporter, reported, reason, reportTime, handler, handleTime, conclusion, conclusionTime, records);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void insertReport(UUID uuid, UUID reporter, UUID reported, String reason, long reportTime, List<ReportDataChatRecord> chat) {
        Report report = new Report(plugin, this, -1, uuid, reporter, reported, reason, reportTime, null, null, null, null, chat);
        try {
            report.init();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
