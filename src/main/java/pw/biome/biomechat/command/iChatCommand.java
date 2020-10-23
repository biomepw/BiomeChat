package pw.biome.biomechat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.biome.biomechat.BiomeChat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@CommandAlias("ichat|biomechat")
@Description("Chat-related commands")
public class iChatCommand extends BaseCommand {

    @Getter
    private static final List<UUID> partyChatUsers = new ArrayList<>();

    @Subcommand("p|party")
    @Description("Toggles party chat mode")
    public void onParty(Player player) {
        if (partyChatUsers.contains(player.getUniqueId())) {
            partyChatUsers.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "Party chat is now " + ChatColor.RED + "off");
        } else {
            partyChatUsers.add(player.getUniqueId());
            player.sendMessage(ChatColor.GOLD + "Party chat is now " + ChatColor.GREEN + "on");
        }
    }

    @Subcommand("reload")
    @CommandPermission("ichat.admin")
    @Description("Reloads plugin")
    public void onReload(CommandSender sender) {
        BiomeChat.getPlugin().reload();
        sender.sendMessage(ChatColor.GREEN + "Plugin reloaded!");
    }
}
