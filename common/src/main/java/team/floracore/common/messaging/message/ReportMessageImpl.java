package team.floracore.common.messaging.message;

import com.google.gson.*;
import org.checkerframework.checker.nullness.qual.*;
import org.floracore.api.messenger.message.type.*;
import team.floracore.common.messaging.*;
import team.floracore.common.util.gson.*;

import java.util.*;

public class ReportMessageImpl extends AbstractMessage implements ReportMessage {
    public static final String TYPE = "report";
    private final UUID reporter;
    private final UUID reportedUser;
    private final String reporterServer;
    private final String reportedUserServer;
    private final String reason;

    public ReportMessageImpl(UUID id, UUID reporter, UUID reportedUser, String reporterServer, String reportedUserServer, String reason) {
        super(id);
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.reporterServer = reporterServer;
        this.reportedUserServer = reportedUserServer;
        this.reason = reason;
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

        String reason = Optional.ofNullable(content.getAsJsonObject().get("reason"))
                .map(JsonElement::getAsString)
                .orElseThrow(() -> new IllegalStateException("Incoming message has no reason argument: " + content));

        return new ReportMessageImpl(id, reporter, reportedUser, reporterServer, reportedUserServer, reason);
    }

    @Override
    public @NonNull String asEncodedString() {
        return FloraCoreMessagingService.encodeMessageAsString(TYPE, getId(),
                new JObject().add("reporter", this.reporter.toString())
                        .add("reportedUser", this.reportedUser.toString())
                        .add("reporterServer", this.reporterServer)
                        .add("reportedUserServer", this.reportedUserServer)
                        .add("reason", this.reason).toJson()
        );
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

    @Override
    public @NonNull String getReason() {
        return this.reason;
    }
}