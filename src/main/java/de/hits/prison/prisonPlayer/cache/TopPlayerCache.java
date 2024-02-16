package de.hits.prison.prisonPlayer.cache;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerCurrencyDao;
import de.hits.prison.base.model.entity.PlayerCurrency;

import java.util.ArrayList;
import java.util.List;

@Component
public class TopPlayerCache {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;

    String currency;
    List<PlayerCurrency> topPlayersCache;

    public TopPlayerCache(String currency) {
        this.currency = currency;
        this.topPlayersCache = new ArrayList<>();
        updateTopPlayers();
    }

    public void updateTopPlayers() {
        this.topPlayersCache = playerCurrencyDao.finder().orderDesc(this.currency).findMax(10);
    }

    public List<PlayerCurrency> getTopPlayerCache() {
        return this.topPlayersCache;
    }

}

