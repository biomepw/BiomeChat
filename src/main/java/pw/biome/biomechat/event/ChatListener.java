package pw.biome.biomechat.event;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import pw.biome.biomechat.command.CommandHandler;
import pw.biome.biomechat.BiomeChat;
import pw.biome.biomechat.obj.PlayerCache;

public class ChatListener implements Listener {

    /**
     * Listener method for player chat
     *
     * @param event AsyncPlayerChatEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        ChatColor rankColour = PlayerCache.getFromUUID(player.getUniqueId()).getRank().getPrefix();

        // Process format
        String newFormat = rankColour + "%1$s" + ChatColor.WHITE + ": %2$s";
        event.setFormat(newFormat);

        // Colourise their message if they have permission!
        if (player.hasPermission("ichat.colour")) {
            message = BiomeChat.getPlugin().colourise(message);
        }

        // Party only chat
        if (CommandHandler.getPartyChatUsers().contains(player.getUniqueId())) {
            handlePartyChat(player, message);
            event.setCancelled(true);
        }

        event.setMessage(message);
    }

    /**
     * Method to handle party only chat
     *
     * @param player  that sent the message
     * @param message to be sent
     */
    private void handlePartyChat(Player player, String message) {
        PlayerCache playerCache = PlayerCache.getFromUUID(player.getUniqueId());

        Bukkit.getScheduler().runTask(BiomeChat.getPlugin(), () -> {
            for (PlayerCache member : playerCache.getRank().getMembers()) {
                Player target = Bukkit.getPlayer(member.getUuid());

                if (target != null) {
                    target.sendMessage(ChatColor.GOLD + "*" + playerCache.getRank().getPrefix() + player.getDisplayName() + ": " + message);
                }
            }
            System.out.println(ChatColor.GOLD + "*" + playerCache.getRank().getPrefix() + player.getName() + ": " + message);
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerCache playerCache = PlayerCache.getOrCreateFromUUID(event.getPlayer().getUniqueId());
        playerCache.updateDisplayName(player);
        player.setDisplayName(playerCache.getDisplayName());
    }
}
