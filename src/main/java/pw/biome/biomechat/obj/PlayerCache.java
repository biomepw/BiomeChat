package pw.biome.biomechat.obj;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import pw.biome.biomechat.BiomeChat;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerCache {

    @Getter
    private static final ConcurrentHashMap<UUID, PlayerCache> playerCaches = new ConcurrentHashMap<>();

    @Getter
    private static final HashMap<UUID, String> nickCache = new HashMap<>();

    @Getter
    private final UUID uuid;

    @Getter
    @Setter
    private Rank rank;

    @Getter
    private String displayName;

    public PlayerCache(Player player) {
        this.uuid = player.getUniqueId();
        this.rank = Rank.getRankFromName(BiomeChat.getPlugin().getPermission().getPrimaryGroup(player));

        updateDisplayName(player);

        rank.addMember(this);

        playerCaches.put(uuid, this);
    }

    public void updateDisplayName(Player player) {
        if (nickCache.get(uuid) != null) {
            this.displayName = nickCache.get(uuid);
        } else {
            this.displayName = player.getDisplayName();
        }
    }

    public void setDisplayName(String newDisplayName) {
        this.displayName = newDisplayName;

        BiomeChat.getPlugin().getConfig().set("playerdata." + uuid.toString(), newDisplayName);
        BiomeChat.getPlugin().saveConfig();
    }

    /**
     * Method to check if the player is AFK
     *
     * @return whether or not the player is AFK
     */
    public boolean isAFK() {
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return false;
        Team team = player.getScoreboard().getTeam("hc_afk");
        if (team == null) return false;

        return team.hasEntry(player.getName());
    }

    public static PlayerCache getFromUUID(UUID uuid) {
        return playerCaches.get(uuid);
    }

    public static PlayerCache getOrCreateFromUUID(UUID uuid) {
        PlayerCache playerCache = playerCaches.get(uuid);

        if (playerCache == null) {
            playerCache = new PlayerCache(Bukkit.getPlayer(uuid));
        } else {
            Rank currentRank = playerCache.getRank();
            Rank newRank = Rank.getRankFromName(BiomeChat.getPlugin().getPermission().getPrimaryGroup(Bukkit.getPlayer(uuid)));

            if (currentRank != newRank) {
                currentRank.removeMember(playerCache);
                newRank.addMember(playerCache);
                playerCache.setRank(newRank);
            }
        }

        return playerCache;
    }
}
