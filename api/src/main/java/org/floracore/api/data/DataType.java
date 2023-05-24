package org.floracore.api.data;

/**
 * 这个类是关于Data数据库中存储的数据类型。
 */
public enum DataType {
    /**
     * 自动同步
     * 例如：Fly
     */
    AUTO_SYNC("auto-sync"),
    /**
     * 功能
     * 例如：Nick
     */
    FUNCTION("function"),
    /**
     * 聊天记录
     */
    CHAT("chat"),
    /**
     * 社交系统
     */
    SOCIAL_SYSTEMS("social-systems"),
    SOCIAL_SYSTEMS_PARTY_HISTORY("social-systems-party-history"),
    /**
     * 社交系统-Party邀请
     */
    SOCIAL_SYSTEMS_PARTY_INVITE("social-systems-party-invite"),
    /**
     * 自定义
     * 用于命令
     */
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