package team.floracore.common.util.craftbukkit.signgui;

@FunctionalInterface
public interface SignCompleteHandler {
    void onSignClose(SignCompletedEvent event);
}