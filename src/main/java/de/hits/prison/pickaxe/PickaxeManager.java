package de.hits.prison.pickaxe;

import de.hits.prison.HitsPrison;
import de.hits.prison.server.helper.Manager;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.autowire.helper.AutowiredManager;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.listener.BlockBreakListener;
import de.hits.prison.pickaxe.listener.PickaxeFlagsListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

@Component
public class PickaxeManager implements Manager {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static HitsPrison main;
    @Autowired
    private static PickaxeUtil pickaxeUtil;

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        // Commands

        // Listener
        pluginManager.registerEvents(new BlockBreakListener(), hitsPrison);
        pluginManager.registerEvents(new PickaxeFlagsListener(), hitsPrison);

        AutowiredManager.register(new PickaxeHelper());
    }
}
