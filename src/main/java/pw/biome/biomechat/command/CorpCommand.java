package pw.biome.biomechat.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import pw.biome.biomechat.obj.Corp;


@CommandAlias("corp|corporation")
@Description("Corporation commands")
public class CorpCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("corp.create")
    @Description("Create a corp")
    public void onCorpCreate(Player player, @Optional String corpName, @Optional String prefix) {
        if (corpName == null || prefix == null) {
            player.sendMessage(ChatColor.RED + "Usage: /corp create <name> <prefix>");
        } else {
            if (Corp.getCorpFromName(corpName).isPresent()) {
                player.sendMessage(ChatColor.RED + "Corp under that name already exists!");
            } else {
                try {
                    ChatColor chatColour = ChatColor.of(prefix);
                    new Corp(corpName, chatColour, true);
                    player.sendMessage(ChatColor.GREEN + "Successfully created a corp named: '" + corpName + "' and a prefix of: '" + prefix + "'!");
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "That isn't a valid colour!");
                }
            }
        }
    }

    @Subcommand("add")
    @CommandPermission("corp.add")
    @CommandCompletion("* * * @players")
    @Description("Add a player to a corp")
    public void onCorpAdd(Player player, Corp corp, OnlinePlayer target) {
        if (corp == null) {
            player.sendMessage(ChatColor.RED + "That corp doesn't exist!");
            return;
        } else if (target == null) {
            player.sendMessage(ChatColor.RED + "That player isn't online or cannot be found!");
            return;
        }
        corp.addMember(target.getPlayer().getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Successfully added '" + target.getPlayer().getDisplayName() + "' to corp: '" + corp.getName() + "'");
    }

    @Subcommand("remove")
    @CommandPermission("corp.remove")
    @CommandCompletion("* * * @players")
    @Description("Remove a player from a corp")
    public void onCorpRemove(Player player, Corp corp, OnlinePlayer target) {
        if (corp == null || target == null) {
            player.sendMessage(ChatColor.RED + "Usage: /corp remove <corp> <player>");
        } else {
            corp.removeMember(target.getPlayer().getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Successfully added '" + target.getPlayer().getDisplayName() + "' to corp: '" + corp.getName() + "'");
        }
    }

    @Subcommand("delete")
    @CommandPermission("corp.delete")
    @Description("Delete a corp")
    public void onCorpDelete(Player player, Corp corp) {
        if (corp == null) {
            player.sendMessage(ChatColor.RED + "Usage: /corp delete <corp>");
        } else {
            corp.delete();
            player.sendMessage(ChatColor.GREEN + "Successfully deleted corp: '" + corp.getName() + "'");
        }
    }

    @Subcommand("list")
    @CommandPermission("corp.list")
    @Description("Lists all corp")
    public void onCorpList(Player player) {
        player.sendMessage(ChatColor.GREEN + "All corps:");
        Corp.getCorpList().forEach(corp -> player.sendMessage(corp.getPrefix() + corp.getName()));
    }
}
