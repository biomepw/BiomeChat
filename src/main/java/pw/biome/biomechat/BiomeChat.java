package pw.biome.biomechat;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import pw.biome.biomechat.command.CommandHandler;
import pw.biome.biomechat.event.ChatListener;
import pw.biome.biomechat.obj.PlayerCache;
import pw.biome.biomechat.obj.Rank;

import java.util.UUID;

public class BiomeChat extends JavaPlugin {

    @Getter
    private static BiomeChat plugin;

    @Getter
    private Permission permission = null;

    @Getter
    private static int scoreboardTaskId;

    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        loadRanks();
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        setupPermissions();
        getCommand("ichat").setExecutor(new CommandHandler());
        restartScoreboardTask();
        buildCache();
    }

    /**
     * Vault setup
     */
    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
    }

    /**
     * Load ranks into memory
     */
    private void loadRanks() {
        for (String rank : getConfig().getKeys(false)) {
            if (!rank.startsWith("playerdata")) new Rank(rank, ChatColor.of(getConfig().getString(rank)));
        }

        ConfigurationSection configurationSection = getConfig().getConfigurationSection("playerdata");

        if (configurationSection != null) {
            configurationSection.getKeys(false).forEach(key -> {
                UUID uuid = UUID.fromString(key);
                String nick = getConfig().getString("playerdata." + key);
                PlayerCache.getNickCache().put(uuid, nick);
            });
        }
    }

    /**
     * Helper method to colourise a string with a combination of both hex and legacy chat colours
     *
     * @param input string to colourise
     * @return colourised string
     */
    public static String colourise(String input) {
        while (input.contains("#")) {
            int index = input.indexOf("#");
            if (input.charAt(index - 1) == '&') {
                String hexSubstring = input.substring(index - 1, index + 7).replaceAll("&", "");

                ChatColor transformed = ChatColor.of(hexSubstring);

                // Apply transformation to original string
                input = input.replaceAll("&" + hexSubstring, transformed + "");
            }
        }

        // Apply legacy transformations at end
        return ChatColor.translateAlternateColorCodes('&', input);
    }


    /**
     * Reload all plugin data
     */
    public void reload() {
        Rank.clearData();
        reloadConfig();
        loadRanks();
        buildCache();
    }

    private void buildCache() {
        getServer().getOnlinePlayers().forEach(player -> PlayerCache.getOrCreateFromUUID(player.getUniqueId()));
    }

    /**
     * Update scoreboards with rank
     */
    public void updateScoreboards() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            ImmutableList<Player> playerList = ImmutableList.copyOf(getServer().getOnlinePlayers());
            for (Player player : playerList) {
                PlayerCache playerCache = PlayerCache.getFromUUID(player.getUniqueId());

                if (playerCache == null) return;

                player.setPlayerListHeader(ChatColor.BLUE + "Biome");

                boolean afk = playerCache.isAFK();
                ChatColor prefix = playerCache.getRank().getPrefix();

                if (afk) {
                    player.setPlayerListName(ChatColor.GRAY + player.getDisplayName());
                } else {
                    player.setPlayerListName(prefix + player.getDisplayName());
                }
            }
        });
    }

    public void stopScoreboardTask() {
        if (scoreboardTaskId != 0) {
            Bukkit.getScheduler().cancelTask(BiomeChat.getScoreboardTaskId());
            scoreboardTaskId = 0;
        }
    }

    public void restartScoreboardTask() {
        if (scoreboardTaskId == 0) {
            scoreboardTaskId = getServer().getScheduler().scheduleSyncRepeatingTask(this, this::updateScoreboards, (10 * 20), (10 * 20));
        }
    }
}
