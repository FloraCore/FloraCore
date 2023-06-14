package team.floracore.common.chat;

import com.google.gson.JsonObject;
import team.floracore.api.data.chat.ChatType;
import team.floracore.common.http.AbstractHttpClient;
import team.floracore.common.http.BytebinClient;
import team.floracore.common.http.UnsuccessfulRequestException;
import team.floracore.common.plugin.FloraCorePlugin;
import team.floracore.common.storage.implementation.StorageImplementation;
import team.floracore.common.storage.misc.floracore.tables.CHAT;
import team.floracore.common.util.gson.GsonProvider;
import team.floracore.common.util.gson.JArray;
import team.floracore.common.util.gson.JObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

/**
 * 聊天提供器
 *
 * @author xLikeWATCHDOG
 */
public class ChatProvider {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z")
            .withZone(ZoneId.systemDefault());

    // the time when the listener was first registered
    private final Instant time = Instant.now();
    private final Uploader uploader;
    private final String target;
    // the number of events we have processed
    private final AtomicInteger counter = new AtomicInteger(0);
    private final List<Data> data = new ArrayList<>();
    private final boolean truncated;

    public ChatProvider(FloraCorePlugin plugin, Uploader uploader, UUID uuid) {
        this.uploader = uploader;
        target = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(uuid);
        truncated = false;
        StorageImplementation storageImplementation = plugin.getStorage().getImplementation();
        for (ChatType value : ChatType.values()) {
            List<CHAT> chats = storageImplementation.selectChat(uuid, value);
            chats.sort(Comparator.comparingLong(CHAT::getTime));
            if (chats.isEmpty()) {
                continue;
            }
            if (value == ChatType.SERVER) {
                for (CHAT chat : chats) {
                    Data d = new Data(value, chat.getParameters());
                    boolean i = true;
                    for (Data di : data) {
                        if (di.type == value && di.details.equalsIgnoreCase(chat.getParameters())) {
                            d = di;
                            i = false;
                            break;
                        }
                    }
                    if (i) {
                        data.add(d);
                    }
                    String ret = DATE_FORMAT.format(Instant.ofEpochMilli(chat.getTime())) + " > " +
                            chat.getMessage();
                    d.content.add(ret);
                }
            } else {
                for (CHAT chat : chats) {
                    String details = chat.getParameters();
                    details = details.isEmpty() ? " " : details;
                    Data d = new Data(value, details);
                    boolean i = true;
                    for (Data di : data) {
                        if (di.type == value && di.details.equalsIgnoreCase(chat.getParameters())) {
                            d = di;
                            i = false;
                            break;
                        }
                    }
                    if (i) {
                        data.add(d);
                    }
                    String ret = DATE_FORMAT.format(Instant.ofEpochMilli(chat.getTime())) + " > " +
                            chat.getMessage();
                    d.content.add(ret);
                }
            }
        }
        counter.set(data.size());
    }

    public ChatProvider(FloraCorePlugin plugin, Uploader uploader, String server) {
        this.uploader = uploader;
        target = server;
        truncated = false;
        StorageImplementation storageImplementation = plugin.getStorage().getImplementation();
        List<CHAT> chats = storageImplementation.selectChatServer(server);
        chats.sort(Comparator.comparingLong(CHAT::getTime));
        Data d = new Data(ChatType.SERVER, server);
        data.add(d);
        for (CHAT chat : chats) {
            String name = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(chat.getUniqueId());
            String ret =
                    DATE_FORMAT.format(Instant.ofEpochMilli(chat.getTime())) + " > " + name + " : " + chat.getMessage();
            d.content.add(ret);
        }
        counter.set(data.size());
    }

    public ChatProvider(FloraCorePlugin plugin, Uploader uploader, ChatType type) {
        this.uploader = uploader;
        target = type.name();
        truncated = false;
        StorageImplementation storageImplementation = plugin.getStorage().getImplementation();
        List<CHAT> chats = storageImplementation.selectChatType(type);
        chats.sort(Comparator.comparingLong(CHAT::getTime));
        Data d = new Data(type, " ");
        data.add(d);
        for (CHAT chat : chats) {
            String name = plugin.getApiProvider().getPlayerAPI().getPlayerRecordName(chat.getUniqueId());
            String ret =
                    DATE_FORMAT.format(Instant.ofEpochMilli(chat.getTime())) + " > " + name + " : " + chat.getMessage();
            d.content.add(ret);
        }
        counter.set(data.size());
    }

    /**
     * 将聊天数据上传到ByteBin并返回url
     *
     * @param bytebin the bytebin instance to upload with
     * @return the url
     */
    public String uploadChatData(BytebinClient bytebin) throws IOException, UnsuccessfulRequestException {
        // retrieve variables
        String time = DATE_FORMAT.format(this.time);

        JObject metadata = new JObject()
                .add("time", time)
                .add("count", new JObject()
                        .add("matched", this.counter.get())
                        .add("total", this.counter.get())
                )
                .add("uploader", new JObject()
                        .add("name", this.uploader.getName())
                        .add("uuid", this.uploader.getUniqueId().toString())
                )
                .add("target", this.target)
                .add("truncated", truncated);

        JArray data = new JArray();
        for (Data i : this.data) {
            data.add(i.toJson());
        }

        JsonObject payload = new JObject()
                .add("metadata", metadata)
                .add("data", data)
                .toJson();

        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(new GZIPOutputStream(bytesOut), StandardCharsets.UTF_8)) {
            GsonProvider.normal().toJson(payload, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytebin.postContent(bytesOut.toByteArray(), AbstractHttpClient.JSON_TYPE).key();
    }

    public static class Uploader {
        private final UUID uuid;
        private final String name;

        public Uploader(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public String getName() {
            return name;
        }
    }

    public static class Data {
        private final ChatType type;
        private final String details;
        private final List<String> content = new ArrayList<>();

        public Data(ChatType type, String details) {
            this.type = type;
            this.details = details;
        }

        public ChatType getType() {
            return type;
        }

        public List<String> getContent() {
            return content;
        }

        public String getDetails() {
            return details;
        }

        public JsonObject toJson() {
            return new JObject()
                    .add("type", type.name().toLowerCase())
                    .add("details", details)
                    .add("content", new JArray()
                            .consume(arr -> arr.addAll(content))
                    ).toJson();
        }
    }
}
