package pw.biome.biomechat.event.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import pw.biome.biomechat.obj.Corp;

@AllArgsConstructor
public class CorpDeleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter
    public Corp deletedCorp;

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
