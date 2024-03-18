package de.hits.prison.base.model.dao;

import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.base.model.entity.PlayerCellsGiving;
import de.hits.prison.base.model.entity.PlayerCellsGiving;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

@Repository
public class PlayerCellsGivingDao extends PrisonRepository<PlayerCellsGiving, Long> {

    public PlayerCellsGivingDao() {
        super(PlayerCellsGiving.class);
    }

    public List<PlayerCellsGiving> findByPlayer(OfflinePlayer player) {
        return findByUuid(player.getUniqueId());
    }

    public List<PlayerCellsGiving> findByUuid(UUID uuid) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerUuid", uuid.toString())
                .findAll();
    }

    public List<PlayerCellsGiving> findByName(String name) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerName", name)
                .findAll();
    }

    public PlayerCellsGiving findByPrisonPlayerAndType(PrisonPlayer prisonPlayer, String type) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerUuid", prisonPlayer.getPlayerUuid())
                .equal("cellsGivingItem", type)
                .findFirst();
    }

}
