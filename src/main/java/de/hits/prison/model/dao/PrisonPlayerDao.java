package de.hits.prison.model.dao;

import de.hits.prison.model.anno.Repository;
import de.hits.prison.model.entity.PrisonPlayer;
import de.hits.prison.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Repository
public class PrisonPlayerDao extends PrisonRepository<PrisonPlayer, Long> {

    public PrisonPlayerDao() {
        super(PrisonPlayer.class);
    }

    public PrisonPlayer findByPlayer(OfflinePlayer player) {
        return findByUuid(player.getUniqueId());
    }

    public PrisonPlayer findByUuid(UUID uuid) {
        return finder().equal("playerUuid", uuid.toString()).findFirst();
    }

    public PrisonPlayer findByName(String name) {
        return finder().equal("playerName", name).findFirst();
    }

}
