package pw.biome.biomechat.event;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pw.biome.biomechat.BiomeChat;
import pw.biome.biomechat.command.iChatCommand;
import pw.biome.biomechat.obj.Corp;
import pw.biome.biomechat.obj.MetadataManager;

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
        UUID uuid = player.getUniqueId();

        Corp corp = Corp.getCorpForUser(uuid);
        boolean isPatron = MetadataManager.getPatrons().contains(uuid);

        // Process format
        String newFormat;
        if (isPatron) {
            newFormat = corp.getPrefix() + "**%s" + ChatColor.WHITE + ": %s";
        } else {
            newFormat = corp.getPrefix() + "%s" + ChatColor.WHITE + ": %s";
        }

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
                    target.sendMessage(ChatColor.GOLD + "~" + corp.getPrefix() + player.getDisplayName() + ": " + message);
                }
            }
            System.out.println(ChatColor.GOLD + "~" + corp.getPrefix() + player.getName() + ": " + message);
        });
    }

    @EventHandler
    private void playerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String nickname = MetadataManager.getNicknameMap().get(player.getUniqueId());

        if (nickname != null) {
            player.setDisplayName(nickname);
            event.setJoinMessage(event.getJoinMessage().replaceAll(player.getName(), nickname));
        }
    }

    @EventHandler
    public void playerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String nickname = MetadataManager.getNicknameMap().get(player.getUniqueId());

        if (nickname != null) {
            event.setQuitMessage(event.getQuitMessage().replaceAll(player.getName(), nickname));
        }
    }
}
