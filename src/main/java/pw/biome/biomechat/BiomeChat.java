package pw.biome.biomechat;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import pw.biome.biomechat.command.CorpCommand;
import pw.biome.biomechat.command.iChatCommand;
import pw.biome.biomechat.event.ChatListener;
import pw.biome.biomechat.obj.Corp;
import pw.biome.biomechat.obj.ScoreboardHook;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BiomeChat extends JavaPlugin {

    @Getter
    private static BiomeChat plugin;

    @Getter
    private static int scoreboardTaskId;

    @Getter
    private final List<ScoreboardHook> scoreboardHookList = new ArrayList<>();

    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        loadRanks();
        getServer().getPluginManager().registerEvents(new ChatListener(), plugin);

        PaperCommandManager manager = new PaperCommandManager(plugin);
        manager.getCommandContexts().registerContext(Corp.class, Corp.getContextResolver());
        manager.registerCommand(new CorpCommand());
        manager.registerCommand(new iChatCommand());

        restartScoreboardTask();

        // Run task to cleanup all empty corps
        getServer().getScheduler().runTaskLater(plugin, () -> {
            for (Corp corp : Corp.getCorpList()) {
                // Don't delete the default corp...
                if (corp == Corp.DEFAULT_CORP) continue;
                if (corp.getMembers().isEmpty()) {
                    getLogger().info("Deleting corp " + corp.getName() + " as it is empty.");
                    corp.delete();
                }
            }
        }, 20 * 10);
    }

    /**
     * Load ranks into memory
     */
    private void loadRanks() {
        ConfigurationSection corpConfigurationSection = getConfig().getConfigurationSection("corps");
        if (corpConfigurationSection != null) {
            corpConfigurationSection.getKeys(false).forEach(key -> {
                String prefix = corpConfigurationSection.getString(key + ".prefix");
                List<String> members = corpConfigurationSection.getStringList(key + ".members");

                if (key != null && prefix != null) {
                    Corp corp = new Corp(key, ChatColor.of(prefix));

                    // Load default corp
                    if (corp.getName().equals("default")) {
                        Corp.DEFAULT_CORP = corp;
                        return;
                    }

                    members.forEach(string -> {
                        UUID uuid = UUID.fromString(string);
                        corp.addMember(uuid);
                    });
                }
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
            if (index != 0 && input.charAt(index - 1) == '&') {
                String hexSubstring = input.substring(index - 1, index + 7).replaceAll("&", "");

                try {
                    ChatColor transformed = ChatColor.of(hexSubstring);
                    // Apply transformation to original string
                    input = input.replaceAll("&" + hexSubstring, transformed + "");
                } catch (IllegalArgumentException ignored) {

                }
            } else {
                break;
            }
        }

        // Apply legacy transformations at end
        return ChatColor.translateAlternateColorCodes('&', input);
    }


    /**
     * Reload all plugin data
     */
    public void reload() {
        Corp.clearData();
        reloadConfig();
        loadRanks();
    }

    /**
     * Update scoreboards with rank
     */
    public void updateScoreboards() {
        for (Player player : getServer().getOnlinePlayers()) {
            player.setPlayerListHeader(ChatColor.BLUE + "Biome");

            Corp corp = Corp.getCorpForUser(player.getUniqueId());
            ChatColor prefix = corp.getPrefix();
            boolean afk = isAFK(player);

            if (afk) {
                player.setPlayerListName(ChatColor.GRAY + player.getDisplayName());
            } else {
                player.setPlayerListName(prefix + player.getDisplayName());
            }
        }
    }

    public void stopScoreboardTask() {
        if (scoreboardTaskId != 0) {
            Bukkit.getScheduler().cancelTask(scoreboardTaskId);
            scoreboardTaskId = 0;
        }
    }

    public void restartScoreboardTask() {
        if (scoreboardTaskId == 0 && scoreboardHookList.size() == 0) {
            scoreboardTaskId = getServer().getScheduler().runTaskTimerAsynchronously(this, this::updateScoreboards, (10 * 20), (10 * 20)).getTaskId();
        }
    }

    public void registerHook(ScoreboardHook hook) {
        scoreboardHookList.add(hook);
    }

    public void unregisterHook(ScoreboardHook hook) {
        scoreboardHookList.remove(hook);
    }

    /**
     * Method to check if the player is AFK
     *
     * @return whether or not the player is AFK
     */
    public boolean isAFK(Player player) {
        if (player != null) {
            Team team = player.getScoreboard().getTeam("hc_afk");
            if (team != null) {
                return team.hasEntry(player.getName());
            }
        }
        return false;
    }
}
