package org.floracore.api.data;

public enum DataType {
    AUTO_SYNC("auto-sync"),
    FUNCTION("function"),
    STAGING_DATA("staging-data"),
    CUSTOM("custom");
    final String name;

    DataType(String name) {
        this.name = name;
    }

    public static DataType parse(String name, DataType def) {
        for (DataType t : values()) {
            if (t.getName().equalsIgnoreCase(name)) {
                return t;
            }
        }
        return def;
    }

    public static DataType parse(String name) {
        return parse(name, AUTO_SYNC);
    }

    public String getName() {
        return name;
    }
}