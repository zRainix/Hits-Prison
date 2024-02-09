package de.hits.prison.model.dao;

import de.hits.prison.model.anno.Repository;
import de.hits.prison.model.entity.PlayerCurrency;
import de.hits.prison.model.helper.CriteriaQueryBuilder;
import de.hits.prison.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.List;
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

    public List<PlayerCurrency> findTopPlayersByCategory(String category, int max) {
        CriteriaQueryBuilder<PlayerCurrency> queryBuilder = finder();
        List<PlayerCurrency> topPlayers = queryBuilder
                .orderDesc(category)
                .findMax(max);

        return topPlayers;
    }

}
