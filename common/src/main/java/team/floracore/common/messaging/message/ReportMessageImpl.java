package team.floracore.common.messaging.message;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.messaging.*;

import java.util.*;

public class ReportMessageImpl extends AbstractMessage implements ReportMessage {
    public static final String TYPE = "report";
    private final UUID reporter;
    private final UUID reportedUser;
    private final String reporterServer;
    private final String reportedUserServer;

    public ReportMessageImpl(UUID id, UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer) {
        super(id);
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reporterServer = reporterServer;
        this.reportedUserServer = reportedUserServer;
    }

    public static ReportMessageImpl decode(@Nullable JsonElement content, UUID id) {
        if (content == null) {
            throw new IllegalStateException("Missing content");
        }
        UUID reporter = Optional.ofNullable(content.getAsJsonObject().get("reporter"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no reporter argument: " + content));

        UUID reportedUser = Optional.ofNullable(content.getAsJsonObject().get("reportedUser"))
                .map(JsonElement::getAsString)
                .map(UUID::fromString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no reportedUser argument: " + content));

        String reporterServer = Optional.ofNullable(content.getAsJsonObject().get("reporterServer"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no reporterServer argument: " + content));

        String reportedUserServer = Optional.ofNullable(content.getAsJsonObject().get("reportedUserServer"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no reportedUserServer argument: " + content));

        return new ReportMessageImpl(id, reporter, reportedUser, reporterServer, reportedUserServer);
    }

    @Override
    public @NonNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(), null);
    }

    @Override
    public @NonNull UUID getReporter() {
        return this.reporter;
    }

    @Override
    public @NonNull UUID getReportedUser() {
        return this.reportedUser;
    }

    @Override
    public @NonNull String getReportedUserServer() {
        return this.reportedUserServer;
    }

    @Override
    public @NonNull String getReporterServer() {
        return this.reporterServer;
    }
}
