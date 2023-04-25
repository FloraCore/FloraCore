package org.floracore.api.server;

public enum ServerType {
    NORMAL("normal", false, true),
    LOBBY("lobby", true, false),
    GAME("game", false, true),
    UNKNOWN("unknown", true, false);
    final String name;
    final boolean autoSync1;
    final boolean autoSync2;

    ServerType(String name, boolean autoSync1, boolean autoSync2) {
        this.name = name;
        this.autoSync1 = autoSync1;
        this.autoSync2 = autoSync2;
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

    public boolean isAutoSync1() {
        return autoSync1;
    }

    public boolean isAutoSync2() {
        return autoSync2;
    }
}