package team.floracore.paper.messaging;

import team.floracore.common.messaging.*;
import team.floracore.paper.*;

public class BukkitMessagingFactory extends MessagingFactory<FCBukkitPlugin> {
    public BukkitMessagingFactory(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }
}
