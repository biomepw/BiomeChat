package pw.biome.biomechat.event;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import pw.biome.biomechat.BiomeChat;
import pw.biome.biomechat.command.iChatCommand;
import pw.biome.biomechat.obj.Corp;

import java.util.UUID;

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

        Corp corp = Corp.getCorpForUser(player.getUniqueId());

        // Process format
        String newFormat = corp.getPrefix() + "%1$s" + ChatColor.WHITE + ": %2$s";
        event.setFormat(newFormat);

        // Colourise their message if they have permission!
        if (player.hasPermission("ichat.colour")) {
            message = BiomeChat.colourise(message);
        }

        // Party only chat
        if (iChatCommand.getPartyChatUsers().contains(player.getUniqueId())) {
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
        Corp corp = Corp.getCorpForUser(player.getUniqueId());
        Bukkit.getScheduler().runTask(BiomeChat.getPlugin(), () -> {
            for (UUID member : corp.getMembers()) {
                Player target = Bukkit.getPlayer(member);

                if (target != null) {
                    target.sendMessage(ChatColor.GOLD + "*" + corp.getPrefix() + player.getDisplayName() + ": " + message);
                }
            }
            System.out.println(ChatColor.GOLD + "*" + corp.getPrefix() + player.getName() + ": " + message);
        });
    }
}
