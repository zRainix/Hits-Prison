package de.hits.prison.base.model.dao;

import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.base.model.entity.MineTrustedPlayer;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.base.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

@Repository
public class MineTrustedPlayerDao extends PrisonRepository<MineTrustedPlayer, Long> {

    public MineTrustedPlayerDao() {
        super(MineTrustedPlayer.class);
    }

    public List<MineTrustedPlayer> findByPlayer(OfflinePlayer player) {
        return findByUuid(player.getUniqueId());
    }

    public List<MineTrustedPlayer> findByPrisonPlayer(PrisonPlayer player) {
        return findByUuid(UUID.fromString(player.getPlayerUuid()));
    }

    public MineTrustedPlayer findByPrisonPlayerAndPlayerMineOwner(PrisonPlayer player, PrisonPlayer owner) {
        return finder()
                .join(root -> root.join("refPlayerMine"))
                .join(root -> root.join("refTrustedPrisonPlayer"))
                .equal("refTrustedPrisonPlayer.playerUuid", player.getPlayerUuid())
                .equal("refPlayerMine.refPrisonPlayer.playerUuid", owner.getPlayerUuid())
                .findFirst();
    }

    public MineTrustedPlayer findByPrisonPlayerAndPlayerMine(PrisonPlayer player, PlayerMine playerMine) {
        return finder()
                .join(root -> root.join("refPlayerMine"))
                .join(root -> root.join("refTrustedPrisonPlayer"))
                .equal("refTrustedPrisonPlayer.playerUuid", player.getPlayerUuid())
                .equal("refPlayerMine.id", playerMine.getId())
                .findFirst();
    }

    public List<MineTrustedPlayer> findByUuid(UUID uuid) {
        return finder()
                .join(root -> root.join("refTrustedPrisonPlayer"))
                .equal("refTrustedPrisonPlayer.playerUuid", uuid.toString())
                .findAll();
    }

    public List<MineTrustedPlayer> findByName(String name) {
        return finder()
                .join(root -> root.join("refTrustedPrisonPlayer"))
                .equal("refTrustedPrisonPlayer.playerName", name)
                .findAll();
    }

}
