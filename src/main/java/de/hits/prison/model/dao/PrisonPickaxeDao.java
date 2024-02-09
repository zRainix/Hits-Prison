package de.hits.prison.model.dao;

import de.hits.prison.model.entity.PrisonPickaxe;
import de.hits.prison.model.helper.Repository;
import org.bukkit.entity.Player;

import java.util.List;

public class PrisonPickaxeDao extends Repository<PrisonPickaxe, Long> {

    public PrisonPickaxeDao() {
        super(PrisonPickaxe.class);
    }

    public PrisonPickaxe findByPlayer(Player player, String pickaxeId) {
        return finder()
                .join(root -> root.join("refPlayer"))
                .equal("refPlayer.playerUuid", player.getUniqueId().toString())
                .equal("pickaxeId", pickaxeId)
                .findFirst();
    }
}
