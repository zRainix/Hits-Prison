package de.hits.prison.mine;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.mine.helper.MineHelper;
import org.bukkit.plugin.PluginManager;

public class MineManager implements Manager {
    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        AutowiredManager.register(new MineHelper());
    }
}
