package de.hits.prison.mechanic.prisonPlayer;

import de.hits.prison.HitsPrison;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.autowire.helper.AutowiredManager;
import de.hits.prison.mechanic.helper.BaseManager;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerExpCache;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerObsidianShardsCache;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerVulcanicAshCache;
import de.hits.prison.mechanic.prisonPlayer.command.ExpCommand;
import de.hits.prison.mechanic.prisonPlayer.command.ObsidianShardsCommand;
import de.hits.prison.mechanic.prisonPlayer.command.VulcanicAshCommand;
import de.hits.prison.mechanic.prisonPlayer.listener.PrisonPlayerListener;
import org.bukkit.plugin.PluginManager;

@Component
public class PrisonPlayerManager implements BaseManager {

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        // Commands:
        VulcanicAshCommand vulcanicAshCommand = new VulcanicAshCommand();
        hitsPrison.getCommand("ash").setExecutor(vulcanicAshCommand);

        ObsidianShardsCommand obsidianShardCommand = new ObsidianShardsCommand();
        hitsPrison.getCommand("shards").setExecutor(obsidianShardCommand);

        ExpCommand expCommand = new ExpCommand();
        hitsPrison.getCommand("exp").setExecutor(expCommand);

        // Listener:
        PrisonPlayerListener prisonPlayerListener = new PrisonPlayerListener();
        pluginManager.registerEvents(prisonPlayerListener, hitsPrison);

        AutowiredManager.register(new TopPlayerExpCache());
        AutowiredManager.register(new TopPlayerObsidianShardsCache());
        AutowiredManager.register(new TopPlayerVulcanicAshCache());

    }
}
