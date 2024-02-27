package de.hits.prison.base.model.dao;

import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Repository
public class PlayerMineDao extends PrisonRepository<PlayerMine, Long> {

    public PlayerMineDao() {
        super(PlayerMine.class);
    }

    public PlayerMine findByPlayer(OfflinePlayer player) {
        return findByUuid(player.getUniqueId());
    }

    public PlayerMine findByPrisonPlayer(PrisonPlayer player) {
        return findByUuid(UUID.fromString(player.getPlayerUuid()));
    }

    public PlayerMine findByUuid(UUID uuid) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerUuid", uuid.toString())
                .findFirst();
    }

    public PlayerMine findByName(String name) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerName", name)
                .findFirst();
    }

}
