package org.floracore.api.bungee.chat;

public interface ChannelsAPI {
    void add(ChatChannel chatChannel);

    ChatChannel parse(String identifierIn) throws IllegalArgumentException;
}
