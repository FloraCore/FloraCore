package team.floracore.bungee;

import com.google.gson.*;
import team.floracore.common.messaging.*;

import java.util.*;

public class BungeeMessagingFactory extends MessagingFactory<FCBungeePlugin> {
    public BungeeMessagingFactory(FCBungeePlugin plugin) {
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
