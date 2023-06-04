package org.floracore.api.bungee.chat;

import java.util.List;

public class ChatChannel {
    private final String key;
    private final String name;
    private final boolean enableChatColor;
    private final List<String> commands;
    private final List<String> permissions;
    private final String[] identifiers;

    public ChatChannel(String key, String name, boolean enableChatColor, List<String> commands, List<String> permissions, String... identifiers) {
        this.key = key;
        this.name = name;
        this.enableChatColor = enableChatColor;
        this.commands = commands;
        this.permissions = permissions;
        this.identifiers = identifiers;
    }

    public String getKey() {
        return key;
    }

    public boolean enableChatColor() {
        return enableChatColor;
    }

    public List<String> getCommands() {
        return commands;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public String getName() {
        return name;
    }

    public String[] getIdentifiers() {
        return identifiers;
    }
}
