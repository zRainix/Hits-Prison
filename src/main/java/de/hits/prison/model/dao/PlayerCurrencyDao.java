package de.hits.prison.model.dao;

import de.hits.prison.model.entity.PlayerCurrency;
import de.hits.prison.model.helper.Repository;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerCurrencyDao extends Repository<PlayerCurrency, Long> {

    public PlayerCurrencyDao() {
        super(PlayerCurrency.class);
    }

    public PlayerCurrency findByPlayer(OfflinePlayer player) {
        return findByUuid(player.getUniqueId());
    }

    public PlayerCurrency findByUuid(UUID uuid) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerUuid", uuid.toString())
                .findFirst();
    }

    public PlayerCurrency findByName(String name) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerName", name)
                .findFirst();
    }
}
