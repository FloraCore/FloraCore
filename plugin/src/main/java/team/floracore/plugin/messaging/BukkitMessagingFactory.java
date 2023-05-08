package team.floracore.plugin.messaging;

import team.floracore.common.messaging.*;
import team.floracore.plugin.*;

public class BukkitMessagingFactory extends MessagingFactory<FCBukkitPlugin> {
    public BukkitMessagingFactory(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }
}
