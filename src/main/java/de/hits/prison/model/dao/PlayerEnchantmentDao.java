package de.hits.prison.model.dao;

import de.hits.prison.model.anno.Repository;
import de.hits.prison.model.entity.PlayerEnchantment;
import de.hits.prison.model.helper.PrisonRepository;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.UUID;

@Repository
public class PlayerEnchantmentDao extends PrisonRepository<PlayerEnchantment, Long> {

    public PlayerEnchantmentDao() {
        super(PlayerEnchantment.class);
    }

    public List<PlayerEnchantment> findAllByPlayer(OfflinePlayer player) {
        return findAllByUuid(player.getUniqueId());
    }

    public List<PlayerEnchantment> findAllByUuid(UUID uuid) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerUuid", uuid.toString())
                .findAll();
    }

    public List<PlayerEnchantment> findAllByName(String name) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("refPrisonPlayer.playerName", name)
                .findAll();
    }

    public PlayerEnchantment findByPlayerAndEnchantmentName(OfflinePlayer player, String enchantmentName) {
        return findByUuidAndEnchantmentName(player.getUniqueId(), enchantmentName);
    }

    public PlayerEnchantment findByUuidAndEnchantmentName(UUID uuid, String enchantmentName) {
        return finder()
                .join(root -> root.join("refPrisonPlayer"))
                .equal("enchantmentName", enchantmentName)
                .equal("refPrisonPlayer.playerUuid", uuid.toString())
                .findFirst();
    }
}
