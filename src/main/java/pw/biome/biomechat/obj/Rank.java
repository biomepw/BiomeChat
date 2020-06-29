package pw.biome.biomechat.obj;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Rank {

    private static final List<Rank> rankList = new ArrayList<>();

    @Getter
    private final String name;

    @Getter
    private final ChatColor prefix;

    @Getter
    private final List<PlayerCache> members = new ArrayList<>();

    public Rank(String name, ChatColor prefix) {
        this.name = name;
        this.prefix = prefix;
        rankList.add(this);
    }

    public void addMember(PlayerCache playerCache) {
        members.add(playerCache);
    }

    public void removeMember(PlayerCache playerCache) {
        members.remove(playerCache);
    }

    public static Rank getRankFromName(String rankName) {
        for (Rank rank : rankList) {
            if (rank.getName().equals(rankName)) return rank;
        }
        return null;
    }

    public static void clearData() {
        rankList.forEach(rank -> rank.getMembers().clear());
        rankList.clear();
    }
}
