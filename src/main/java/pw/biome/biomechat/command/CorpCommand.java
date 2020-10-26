package pw.biome.biomechat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import pw.biome.biomechat.obj.Corp;


@CommandAlias("corp|corporation")
@Description("Corporation commands")
public class CorpCommand extends BaseCommand {

    @Default
    @Subcommand("help")
    @Description("Shows the help screen")
    public void onHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Hey there and welcome to Corps! For some more detail, please read up at");
        sender.sendMessage(ChatColor.BLUE + "https://github.com/biomepw/biome.pw/wiki");
    }

    @Subcommand("create")
    @CommandPermission("corp.create")
    @Description("Create a corp")
    public void onCorpCreate(CommandSender sender, String corpName, String prefix) {
        if (corpName == null || prefix == null) {
            sender.sendMessage(ChatColor.RED + "Usage: /corp create <name> <prefix>");
        } else {
            if (Corp.getCorpFromName(corpName).isPresent()) {
                sender.sendMessage(ChatColor.RED + "Corp under that name already exists!");
            } else {
                try {
                    ChatColor chatColour = ChatColor.of(prefix);
                    new Corp(corpName, chatColour, true);
                    sender.sendMessage(ChatColor.GREEN + "Successfully created a corp named: '" + corpName + "' and a prefix of: '" + prefix + "'!");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "That isn't a valid colour!");
                }
            }
        }
    }

    @Subcommand("add")
    @CommandPermission("corp.add")
    @CommandCompletion("* * * @players")
    @Description("Add a player to a corp")
    public void onCorpAdd(CommandSender sender, Corp corp, OnlinePlayer target) {
        if (corp == null) {
            sender.sendMessage(ChatColor.RED + "That corp doesn't exist!");
            return;
        } else if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player isn't online or cannot be found!");
            return;
        }
        corp.addMember(target.getPlayer().getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Successfully added '" + target.getPlayer().getDisplayName() + "' to corp: '" + corp.getName() + "'");
    }

    @Subcommand("remove")
    @CommandPermission("corp.remove")
    @CommandCompletion("* * * @players")
    @Description("Remove a player from a corp")
    public void onCorpRemove(CommandSender sender, Corp corp, OnlinePlayer target) {
        if (corp == null || target == null) {
            sender.sendMessage(ChatColor.RED + "Usage: /corp remove <corp> <player>");
        } else {
            corp.removeMember(target.getPlayer().getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "Successfully added '" + target.getPlayer().getDisplayName() + "' to corp: '" + corp.getName() + "'");
        }
    }

    @Subcommand("delete")
    @CommandPermission("corp.delete")
    @Description("Delete a corp")
    public void onCorpDelete(CommandSender sender, Corp corp) {
        if (corp == null) {
            sender.sendMessage(ChatColor.RED + "Usage: /corp delete <corp>");
        } else {
            corp.delete();
            sender.sendMessage(ChatColor.GREEN + "Successfully deleted corp: '" + corp.getName() + "'");
        }
    }

    @Subcommand("list")
    @CommandPermission("corp.list")
    @Description("Lists all corp")
    public void onCorpList(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "All corps:");
        Corp.getCorpList().forEach(corp -> sender.sendMessage(corp.getPrefix() + corp.getName()));
    }
}
