package team.floracore.common.locale.chat;

import com.google.gson.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.floracore.api.data.*;
import org.floracore.api.data.chat.*;
import team.floracore.common.plugin.*;
import team.floracore.common.storage.misc.floracore.tables.*;

import java.util.*;

public class ChatManager implements Listener {
    private final FloraCorePlugin plugin;
    private final Chat chat;
    private final HashMap<UUID, MapPlayerRecord> players = new HashMap<>();

    public ChatManager(FloraCorePlugin plugin) {
        this.plugin = plugin;
        long startTime = System.currentTimeMillis();
        plugin.getStorage().getImplementation().insertChat(plugin.getServerName(), startTime);
        this.chat = plugin.getStorage().getImplementation().selectChatWithStartTime(plugin.getServerName(), startTime);
        plugin.getListenerManager().registerListener(this);
        plugin.getBootstrap().getScheduler().async().execute(() -> {
            List<Chat> ret = plugin.getStorage().getImplementation().selectChat(plugin.getServerName());
            for (Chat i : ret) {
                if (i.getId() != this.chat.getId()) {
                    if (i.getEndTime() <= 0) {
                        i.setEndTime(startTime);
                    }
                }
            }
        });
    }

    public Chat getChat() {
        return chat;
    }

    public FloraCorePlugin getPlugin() {
        return plugin;
    }

    public void shutdown() {
        long endTime = System.currentTimeMillis();
        this.chat.setEndTime(endTime);
    }

    public void addChatRecord(ChatRecord chatRecord) {
        List<ChatRecord> chatRecords = this.chat.getRecords();
        chatRecords.add(chatRecord);
        this.chat.setRecords(chatRecords);
    }

    public UUID getPlayerChatUUID(Player player) {
        UUID uuid = player.getUniqueId();
        return getPlayerChatUUID(uuid);
    }

    public UUID getPlayerChatUUID(UUID uuid) {
        return players.get(uuid).getChatUUID();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        long time = System.currentTimeMillis();
        String message = e.getMessage();
        int id = this.chat.getRecords().size() + 1;
        ChatRecord chatRecord = new ChatRecord(id, uuid, message, time);
        this.plugin.getBootstrap().getScheduler().async().execute(() -> addChatRecord(chatRecord));
    }

    public void clearPlayerChatsInvalid(UUID uuid) {
        List<Data> ret = plugin.getStorage().getImplementation().getSpecifiedTypeData(uuid, DataType.CHAT);
        for (Data data : ret) {
            if (data.getValue().isEmpty()) {
                plugin.getStorage().getImplementation().deleteDataID(data.getId());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        long time = System.currentTimeMillis();
        UUID chatUUID = UUID.randomUUID();
        MapPlayerRecord mapPlayerRecord = new MapPlayerRecord(time, chatUUID);
        players.put(uuid, mapPlayerRecord);
        this.plugin.getBootstrap().getScheduler().async().execute(() -> {
            this.plugin.getStorage().getImplementation().insertData(uuid, DataType.CHAT, chatUUID.toString(), "", 0);
            clearPlayerChatsInvalid(uuid);
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        MapPlayerRecord mapPlayerRecord = players.get(uuid);
        long joinTime = mapPlayerRecord.getJoinTime();
        long quitTime = System.currentTimeMillis();
        players.remove(uuid);
        PlayerChatRecord playerChatRecord = new PlayerChatRecord(this.chat.getId(), joinTime, quitTime);
        Gson gson = new Gson();
        String recordsJson = gson.toJson(playerChatRecord);
        this.plugin.getBootstrap().getScheduler().async().execute(() -> {
            this.plugin.getStorage().getImplementation().insertData(uuid, DataType.CHAT, mapPlayerRecord.getChatUUID().toString(), recordsJson, 0);
        });
    }

    public static class MapPlayerRecord {
        private final long joinTime;
        private final UUID chatUUID;

        public MapPlayerRecord(long joinTime, UUID chatUUID) {
            this.joinTime = joinTime;
            this.chatUUID = chatUUID;
        }

        public long getJoinTime() {
            return joinTime;
        }

        public UUID getChatUUID() {
            return chatUUID;
        }
    }
}
