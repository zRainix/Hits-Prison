package de.hits.prison.mechanic.pickaxe.helper;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.model.dao.PlayerEnchantmentDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import de.hits.prison.model.entity.PlayerEnchantment;
import de.hits.prison.model.entity.PrisonPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

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

        ItemStack pickaxeItemStack = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta pickaxeItemMeta = buildPlayerMetaData(player, pickaxeItemStack);

        pickaxeItemStack.setItemMeta(pickaxeItemMeta);

        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(pickaxeItemStack);
        NBTTagCompound tag = (item.u() ? item.v() : new NBTTagCompound());

        tag.a("PickaxeUUID", UUID.fromString(prisonPlayer.getPlayerUuid()));
        tag.a("PickaxePrisonPlayerId", prisonPlayer.getId());

        item.b(tag);

        return CraftItemStack.asBukkitCopy(item);
    }

    private ItemMeta buildPlayerMetaData(Player player, ItemStack base) {
        Damageable pickaxeItemMeta = (Damageable) base.getItemMeta();

        pickaxeItemMeta.setUnbreakable(true);
        pickaxeItemMeta.setDamage(0);

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null) {
            logger.warning("PrisonPlayer == NULL");
            return pickaxeItemMeta;
        }

        List<PlayerEnchantment> enchantments = prisonPlayer.getPlayerEnchantments();

        pickaxeItemMeta.setDisplayName("§7[§b֍§7] §c§l" + player.getName() + " §7[§b֍§7] §c§l");

        List<String> lore = new ArrayList<>();

        for (PlayerEnchantment playerEnchantments : enchantments) {
            PickaxeUtil.PickaxeEnchantment pickaxeEnchantment = getPickaxeEnchantmentFromPlayerEnchantment(playerEnchantments);

            PickaxeUtil.EnchantmentRarity rarity = pickaxeEnchantment != null ? pickaxeEnchantment.getRarity() : new PickaxeUtil.EnchantmentRarity(0, "Undefined", "§c");

            lore.add(rarity.getColorPrefix() + "❚ §7" + playerEnchantments.getEnchantmentName() + " " + playerEnchantments.getEnchantmentLevel());

            if (playerEnchantments.getEnchantmentName().equalsIgnoreCase("Efficiency")) {
                pickaxeItemMeta.addEnchant(Enchantment.DIG_SPEED, playerEnchantments.getEnchantmentLevel(), true);
            }
        }

        pickaxeItemMeta.addItemFlags(ItemFlag.values());

        pickaxeItemMeta.setLore(lore);

        return pickaxeItemMeta;
    }

    private String generateCustomPickaxeIdentifier(PrisonPlayer prisonPlayer) {
        if (prisonPlayer == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Pickaxe");
        sb.append("|");
        sb.append(prisonPlayer.getPlayerUuid());
        sb.append("|");
        sb.append(prisonPlayer.getId());
        return sb.toString();
    }

    private boolean isCustomPickaxe(ItemStack itemStack, PrisonPlayer prisonPlayer) {
        if (itemStack == null || !itemStack.hasItemMeta())
            return false;
        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = item.v();

        if (tag == null)
            return false;

        UUID uuid;
        Long prisonPlayerId;
        try {
            uuid = tag.a("PickaxeUUID");
            prisonPlayerId = tag.i("PickaxePrisonPlayerId");
        } catch (Exception e) {
            return false;
        }

        if (uuid == null)
            return false;

        if (prisonPlayerId == null || prisonPlayerId == 0L)
            return false;

        if (prisonPlayer != null) {
            if (!uuid.toString().equals(prisonPlayer.getPlayerUuid()))
                return false;

            if (prisonPlayerId != prisonPlayer.getId())
                return false;
        }

        return true;
    }

    public void checkPlayerPickaxe(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
        PlayerInventory inventory = player.getInventory();
        boolean foundOwn = false;
        for (int slot = 0; slot < inventory.getContents().length; slot++) {
            ItemStack itemStack = inventory.getItem(slot);
            if (isCustomPickaxe(itemStack, null)) {
                if (isCustomPickaxe(itemStack, prisonPlayer) && !foundOwn) {
                    itemStack.setItemMeta(buildPlayerMetaData(player, itemStack));
                    foundOwn = true;
                } else {
                    inventory.setItem(slot, null);
                }
            }
        }
        if (foundOwn)
            return;
        inventory.addItem(buildPlayerPickaxe(player));
    }

    public PickaxeUtil.PickaxeEnchantment getPickaxeEnchantmentFromPlayerEnchantment(PlayerEnchantment playerEnchantment) {
        return pickaxeUtil.getPickaxeEnchantment(playerEnchantment.getEnchantmentName());
    }
}
