package de.hits.prison.base.model.dao;

import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.base.model.entity.CellPlayer;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Repository
public class CellPlayerDao extends PrisonRepository<CellPlayer, Long> {

    public CellPlayerDao() {
        super(CellPlayer.class);
    }

    public CellPlayer findByPlayer(OfflinePlayer player) {
        return findByUuid(player.getUniqueId());
    }

    public CellPlayer findByPrisonPlayer(PrisonPlayer player) {
        return findByUuid(UUID.fromString(player.getPlayerUuid()));
    }

    public CellPlayer findByUuid(UUID uuid) {
        return finder()
                    .join(root -> root.join("refPrisonPlayer"))
                    .equal("refPrisonPlayer.playerUuid", uuid.toString())
                    .findFirst();
    }

    public CellPlayer findByName(String name) {
        return finder().equal("playerName", name).findFirst();
    }
}
