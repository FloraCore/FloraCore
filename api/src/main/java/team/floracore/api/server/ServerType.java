package team.floracore.api.server;

public enum ServerType {
    NORMAL("normal", true),
    LOBBY("lobby", false),
    GAME("game", true),
    UNKNOWN("unknown", false);
    final String name;
    final boolean autoSync;

    ServerType(String name, boolean autoSync) {
        this.name = name;
        this.autoSync = autoSync;
    }

    public static ServerType parse(String name, ServerType def) {
        for (ServerType t : values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return def;
    }

    public static ServerType parse(String name) {
        return parse(name, UNKNOWN);
    }

    public String getName() {
        return name;
    }

    public boolean isAutoSync() {
        return autoSync;
    }
}