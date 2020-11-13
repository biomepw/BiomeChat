package pw.biome.biomechat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pw.biome.biomechat.BiomeChat;
import pw.biome.biomechat.obj.MetadataManager;

import java.util.HashSet;
import java.util.UUID;

@CommandAlias("ichat|biomechat")
@Description("Chat-related commands")
public class iChatCommand extends BaseCommand {

    @Getter
    private static final HashSet<UUID> partyChatUsers = new HashSet<>();

    @Subcommand("p|party")
    @CommandAlias("p|party")
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

    @Subcommand("patron add")
    @CommandPermission("ichat.admin")
    @CommandCompletion("@players")
    @Description("Adds a user as a patron")
    public void onPatronAdd(CommandSender sender, OfflinePlayer player) {
        MetadataManager.getPatrons().add(player.getUniqueId());
        sender.sendMessage(ChatColor.RED + player.getName() + ChatColor.GREEN + " has been added to patrons!");
        BiomeChat.getPlugin().saveMetadata();
    }

    @Subcommand("patron remove")
    @CommandPermission("ichat.admin")
    @CommandCompletion("@patrons")
    @Description("Removes a user as a patron")
    public void onPatronRemove(CommandSender sender, OfflinePlayer player) {
        MetadataManager.getPatrons().remove(player.getUniqueId());
        sender.sendMessage(ChatColor.RED + player.getName() + ChatColor.GREEN + " has been removed from patrons!");
        BiomeChat.getPlugin().saveMetadata();
    }

    @Subcommand("nick")
    @CommandPermission("ichat.admin")
    @CommandCompletion("* * @players *")
    @Description("Sets nickname for user")
    public void onNickname(CommandSender sender, Player player, @Optional String nickname) {
        if (nickname == null) {
            MetadataManager.getNicknameMap().remove(player.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Removed nickname for " + player);
            player.setDisplayName(player.getName());
        } else {
            MetadataManager.getNicknameMap().put(player.getUniqueId(), nickname);
            sender.sendMessage(ChatColor.GREEN + player.getName() + " now has a display name of " + nickname);
            player.setDisplayName(nickname);
        }
        BiomeChat.getPlugin().saveMetadata();
    }

    @Subcommand("debug")
    @CommandPermission("ichat.admin")
    @Description("Debugs")
    public void onDebug(CommandSender sender) {
        BiomeChat.getPlugin().getScoreboardHookList().forEach(hook -> {
            sender.sendMessage(ChatColor.RED + hook.getClass().getName());
        });
    }
}
