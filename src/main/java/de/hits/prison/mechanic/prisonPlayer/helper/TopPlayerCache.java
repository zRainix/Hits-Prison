package de.hits.prison.mechanic.prisonPlayer.helper;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.model.dao.PlayerCurrencyDao;
import de.hits.prison.model.entity.PrisonPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TopPlayerCache {

    @Autowired
    private static PlayerCurrencyDao playerCurrencyDao;

    String currency;
    List<PrisonPlayer> topPlayersCache;

    public TopPlayerCache(String currency) {
        this.currency = currency;
        this.topPlayersCache = new ArrayList<>();
        updateTopPlayers();
    }

    public void updateTopPlayers() {
        this.topPlayersCache = playerCurrencyDao.finder().orderDesc(this.currency).findMax(10).stream().map(top -> top.getRefPrisonPlayer()).collect(Collectors.toList());
    }

    public List<PrisonPlayer> getTopPlayerCache() {
        return this.topPlayersCache;
    }

}

