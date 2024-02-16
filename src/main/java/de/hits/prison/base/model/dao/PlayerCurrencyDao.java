package de.hits.prison.base.model.dao;

import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Repository
public class PlayerCurrencyDao extends PrisonRepository<PlayerCurrency, Long> {

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
