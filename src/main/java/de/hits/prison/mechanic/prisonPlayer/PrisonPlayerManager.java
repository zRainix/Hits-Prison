package de.hits.prison.mechanic.prisonPlayer;

import de.hits.prison.HitsPrison;
import de.hits.prison.mechanic.helper.BaseManager;
import de.hits.prison.mechanic.prisonPlayer.listener.PrisonPlayerListener;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import org.bukkit.plugin.PluginManager;

public class PrisonPlayerManager implements BaseManager {

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        PrisonPlayerDao prisonPlayerDao = hitsPrison.getPrisonPlayerDao();
        PlayerCurrencyDao playerCurrencyDao = hitsPrison.getPlayerCurrencyDao();

        // Commands:

        // Listener:
        PrisonPlayerListener prisonPlayerListener = new PrisonPlayerListener(prisonPlayerDao, playerCurrencyDao);
        pluginManager.registerEvents(prisonPlayerListener, hitsPrison);

        // Scheduler:

    }
}
