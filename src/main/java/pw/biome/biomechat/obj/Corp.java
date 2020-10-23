package pw.biome.biomechat.obj;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import pw.biome.biomechat.BiomeChat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Corp {

    @Getter
    private static final List<Corp> corpList = new ArrayList<>();

    @Getter
    private final String name;

    @Getter
    private final ChatColor prefix;

    @Getter
    private final HashSet<UUID> members = new HashSet<>();

    public static Corp DEFAULT_CORP;

    public Corp(String name, ChatColor prefix) {
        this(name, prefix, false);
    }

    public Corp(String name, ChatColor prefix, boolean save) {
        this.name = name;
        this.prefix = prefix;
        corpList.add(this);
        if (save) saveToConfig();
    }

    public void addMember(UUID uuid) {
        if (this != DEFAULT_CORP) members.add(uuid);
        saveToConfig();
    }

    public void removeMember(UUID uuid) {
        if (this != DEFAULT_CORP) members.remove(uuid);
        saveToConfig();
    }

    public void saveToConfig() {
        FileConfiguration config = BiomeChat.getPlugin().getConfig();

        config.set("corps." + name + ".prefix", prefix.getName());

        // Build members list
        List<String> memberList = new ArrayList<>();
        for (UUID uuid : members) {
            memberList.add(uuid.toString());
        }

        config.set("corps." + name + ".members", memberList);

        BiomeChat.getPlugin().saveConfig();
    }

    public static Optional<Corp> getCorpFromName(String rankName) {
        for (Corp corp : corpList) {
            if (corp.getName().equalsIgnoreCase(rankName)) return Optional.of(corp);
        }
        return Optional.empty();
    }

    public static Corp getCorpForUser(UUID uuid) {
        for (Corp corp : corpList) {
            if (corp.getMembers().contains(uuid)) return corp;
        }
        return DEFAULT_CORP;
    }

    public static ContextResolver<Corp, BukkitCommandExecutionContext> getContextResolver() {
        return (c) -> {
            String corpName = c.popFirstArg();
            Optional<Corp> corpOptional = getCorpFromName(corpName);
            if (corpOptional.isPresent()) {
                return corpOptional.get();
            } else {
                throw new InvalidCommandArgument("No corp found with that name!");
            }
        };
    }

    public void delete() {
        members.forEach(DEFAULT_CORP::addMember);
        members.clear();

        corpList.remove(this);

        // Empty the config
        FileConfiguration config = BiomeChat.getPlugin().getConfig();
        config.set("corps." + name, null);
        BiomeChat.getPlugin().saveConfig();
    }

    public static void clearData() {
        corpList.forEach(corp -> corp.getMembers().clear());
        corpList.clear();
    }
}
