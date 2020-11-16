package pw.biome.biomechat.obj;

import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public final class MetadataManager {

    @Getter
    private static final HashMap<UUID, String> nicknameMap = new HashMap<>();

    @Getter
    private static final HashSet<UUID> patrons = new HashSet<>();
}
