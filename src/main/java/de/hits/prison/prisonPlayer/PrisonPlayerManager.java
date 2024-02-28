package de.hits.prison.prisonPlayer;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerExpCache;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerObsidianShardsCache;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerVolcanicAshCache;
import de.hits.prison.prisonPlayer.command.ExpCommand;
import de.hits.prison.prisonPlayer.command.ObsidianShardsCommand;
import de.hits.prison.prisonPlayer.command.VolcanicAshCommand;
import de.hits.prison.prisonPlayer.listener.PrisonPlayerListener;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import org.bukkit.plugin.PluginManager;

@Component
public class PrisonPlayerManager implements Manager {

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        // Commands:
        VolcanicAshCommand volcanicAshCommand = new VolcanicAshCommand();
        hitsPrison.registerCommand(volcanicAshCommand);

        ObsidianShardsCommand obsidianShardCommand = new ObsidianShardsCommand();
        hitsPrison.registerCommand(obsidianShardCommand);

        ExpCommand expCommand = new ExpCommand();
        hitsPrison.registerCommand(expCommand);

        // Listener:
        PrisonPlayerListener prisonPlayerListener = new PrisonPlayerListener();
        pluginManager.registerEvents(prisonPlayerListener, hitsPrison);

        AutowiredManager.register(new TopPlayerExpCache());
        AutowiredManager.register(new TopPlayerObsidianShardsCache());
        AutowiredManager.register(new TopPlayerVolcanicAshCache());

    }
}
