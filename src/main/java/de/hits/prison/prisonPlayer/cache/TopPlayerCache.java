package de.hits.prison.prisonPlayer.cache;

import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.model.dao.PlayerCurrencyDao;
import de.hits.prison.server.model.entity.PlayerCurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.topPlayersCache = playerCurrencyDao.finder().orderDesc(this.currency).findMax(10).stream().collect(Collectors.toList());
    }

    public List<PlayerCurrency> getTopPlayerCache() {
        return this.topPlayersCache;
    }

}

