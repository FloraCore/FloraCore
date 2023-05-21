package team.floracore.plugin.messaging;

import team.floracore.common.messaging.InternalMessagingService;
import team.floracore.common.messaging.MessagingFactory;
import team.floracore.plugin.FCBukkitPlugin;

public class BukkitMessagingFactory extends MessagingFactory<FCBukkitPlugin> {
    public BukkitMessagingFactory(FCBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }
}
