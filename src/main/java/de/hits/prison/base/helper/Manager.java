package de.hits.prison.base.helper;

import de.hits.prison.HitsPrison;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

public interface Manager {

    void register(HitsPrison hitsPrison, PluginManager pluginManager);

    default EventPriority getPriority() {
        return EventPriority.NORMAL;
    }

}
