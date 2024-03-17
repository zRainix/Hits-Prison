package de.hits.prison.playerList;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.playerList.fileUtil.PlayerListUtil;
import de.hits.prison.playerList.scheduler.PlayerListScheduler;
import org.bukkit.plugin.PluginManager;

@Component
public class PlayerListManager implements Manager {

    @Autowired
    private static PlayerListUtil playerListUtil;
    @Autowired
    private static PlayerListScheduler playerListScheduler;

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        // Commands

        // Listener

        playerListScheduler.setDelay(0);
        playerListScheduler.setPeriod(playerListUtil.getUpdatePeriod());
        playerListScheduler.start();
    }
}
