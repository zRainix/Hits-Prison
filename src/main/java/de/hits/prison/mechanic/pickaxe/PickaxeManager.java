package de.hits.prison.mechanic.pickaxe;

import de.hits.prison.HitsPrison;
import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.helper.BaseManager;
import de.hits.prison.mechanic.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.mechanic.pickaxe.listener.BlockBreakListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

@Component
public class PickaxeManager implements BaseManager {

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
    }
}
