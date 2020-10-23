package pw.biome.biomechat.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.biome.biomechat.BiomeChat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class iChatCommand implements CommandExecutor {

    @Getter
    private static final List<UUID> partyChatUsers = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args) {

        BiomeChat plugin = BiomeChat.getPlugin();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("party") || args[0].equalsIgnoreCase("p")) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;

                    if (partyChatUsers.contains(player.getUniqueId())) {
                        partyChatUsers.remove(player.getUniqueId());

                        player.sendMessage(ChatColor.GOLD + "Party chat is now " + ChatColor.RED + "off");
                    } else {
                        partyChatUsers.add(player.getUniqueId());

                        player.sendMessage(ChatColor.GOLD + "Party chat is now " + ChatColor.GREEN + "on");
                    }
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (commandSender.hasPermission("ichat.reload")) {
                    plugin.reload();
                    commandSender.sendMessage(ChatColor.GREEN + "iChat reloaded!");
                } else {
                    commandSender.sendMessage(ChatColor.GREEN + "iChat v" + plugin.getDescription().getVersion());
                }
            }
        }

        return true;
    }
}
