package de.hits.prison.model.dao;

import de.hits.prison.model.entity.PlayerCurrency;
import de.hits.prison.model.entity.PrisonPlayer;
import de.hits.prison.model.helper.Repository;

public class PlayerCurrencyDao extends Repository<PlayerCurrency, Long> {

    public PlayerCurrencyDao() {
        super(PlayerCurrency.class);
    }

    public PlayerCurrency findById(Long id) {
        return finder().equal("id", id).findFirst();
    }
}
