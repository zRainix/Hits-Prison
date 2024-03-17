package de.hits.prison.mine;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.mine.command.MineCommand;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.listener.*;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

public class MineManager implements Manager {
    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        AutowiredManager.register(new MineHelper());

        // Commands
        hitsPrison.registerCommand(new MineCommand());

        // Listener
        pluginManager.registerEvents(new BlockBreakListener(), hitsPrison);
        pluginManager.registerEvents(new RightClickAirListener(), hitsPrison);
        pluginManager.registerEvents(new RightClickBlockListener(), hitsPrison);
        pluginManager.registerEvents(new RightClickEntityListener(), hitsPrison);

        pluginManager.registerEvents(new MineWorldListener(), hitsPrison);
        pluginManager.registerEvents(new TemplateWorldListener(), hitsPrison);
    }
}
