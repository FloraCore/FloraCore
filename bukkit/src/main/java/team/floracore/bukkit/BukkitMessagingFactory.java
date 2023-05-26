package team.floracore.bukkit;

import com.google.gson.*;
import team.floracore.common.messaging.*;

import java.util.*;

public class BukkitMessagingFactory extends MessagingFactory<FCBukkitPlugin> {
    public BukkitMessagingFactory(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }

    public boolean processIncomingMessage(String type, JsonElement content, UUID id) {
        return false;
    }
}
