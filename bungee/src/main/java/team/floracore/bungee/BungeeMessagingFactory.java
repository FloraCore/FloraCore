package team.floracore.bungee;

import team.floracore.common.messaging.*;

public class BungeeMessagingFactory extends MessagingFactory<FCBungeePlugin> {
    public BungeeMessagingFactory(FCBungeePlugin plugin) {
        super(plugin);
    }

    @Override
    protected InternalMessagingService getServiceFor(String messagingType) {
        return super.getServiceFor(messagingType);
    }
}
