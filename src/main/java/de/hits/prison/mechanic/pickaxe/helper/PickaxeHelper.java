package de.hits.prison.mechanic.pickaxe.helper;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.model.dao.PlayerEnchantmentDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import de.hits.prison.model.entity.PlayerEnchantment;
import de.hits.prison.model.entity.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Component
public class PickaxeHelper {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;

    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;

    @Autowired
    private static PickaxeUtil pickaxeUtil;

    public ItemStack buildPlayerPickaxe(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
        List<PlayerEnchantment> enchantments = prisonPlayer.getPlayerEnchantments();

        // ItemStack bauen
        if(prisonPlayer == null) {
            logger.warning("PrisonPlayer == NULL");
        }

        ItemStack pickaxe = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta pickaxemeta = pickaxe.getItemMeta();

        pickaxemeta.setDisplayName("§7[§b֍§7] §c§l" + player.getName() + " §7[§b֍§7] §c§l");
        pickaxemeta.setLocalizedName(player.getUniqueId().toString());

        List<String> lore = pickaxemeta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        for(PlayerEnchantment playerEnchantments : enchantments) {
            PickaxeEnchantment pickaxeEnchantment = getPickaxeEnchantmentFromPlayerEnchantment(playerEnchantments);
            if(pickaxeEnchantment != null) {
                lore.add("§a❚ §7" + playerEnchantments.getEnchantmentName() + " " + playerEnchantments.getEnchantmentLevel() + (pickaxeEnchantment == null ? " §c(DISABLED)" : ""));
                if(playerEnchantments.getEnchantmentName().equalsIgnoreCase("Efficiency")) {
                    pickaxemeta.addEnchant(Enchantment.DIG_SPEED, playerEnchantments.getEnchantmentLevel(), true);
                }
            }
        }

        pickaxemeta.addItemFlags(ItemFlag.values());
        pickaxemeta.setLore(lore);

        pickaxe.setItemMeta(pickaxemeta);

        return pickaxe;
    }

    private boolean isOwnCustomPickaxe(ItemStack itemStack, UUID playerUUID) {
        if (itemStack != null && itemStack.getType() == Material.DIAMOND_PICKAXE && itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.hasLocalizedName() && itemMeta.getLocalizedName().equals(playerUUID.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean belongsToPlayer(ItemStack itemStack, UUID playerUUID) {
        if(itemStack != null && itemStack.getType() == Material.DIAMOND_PICKAXE & isOwnCustomPickaxe(itemStack, playerUUID)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.hasLocalizedName()) {
                String storedUUIDString = itemMeta.getLocalizedName();
                UUID storedUUID = UUID.fromString(storedUUIDString);

                return storedUUID.equals(playerUUID);
            }
        }
        return false;
    }

    public void checkPlayerPickaxe(Player player) {
        for(ItemStack itemStack : player.getInventory().getContents()) {
            if(isOwnCustomPickaxe(itemStack, player.getUniqueId())) {
                if(belongsToPlayer(itemStack, player.getUniqueId())) {
                    // TODO Wenn ja aktualisieren, wenn Nein löschen
                } else {
                    player.getInventory().remove(itemStack);
                }
            }
        }
    }

    public PickaxeEnchantment getPickaxeEnchantmentFromPlayerEnchantment(PlayerEnchantment playerEnchantment) {
        return pickaxeUtil.getPickaxeEnchantment(playerEnchantment.getEnchantmentName());
    }
}
