package de.hits.prison.pickaxe.helper;

import de.hits.prison.base.util.ItemBuilder;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.apply.ApplyEnchantments;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.model.dao.PlayerEnchantmentDao;
import de.hits.prison.server.model.dao.PrisonPlayerDao;
import de.hits.prison.server.model.entity.PlayerEnchantment;
import de.hits.prison.server.model.entity.PrisonPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Comparator;
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

    private ApplyEnchantments applyEnchantments = new ApplyEnchantments();

    public ItemStack buildPlayerPickaxe(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null) {
            return null;
        }

        ItemBuilder pickaxeBuilder = new ItemBuilder(Material.DIAMOND_PICKAXE);

        pickaxeBuilder.setDisplayName("§7[§b֍§7] §c§l" + player.getName() + " §7[§b֍§7] §c§l");

        prisonPlayer.getPlayerEnchantments().stream().sorted(Comparator.comparingInt(p -> getPickaxeEnchantmentFromPlayerEnchantment(p) != null ? getPickaxeEnchantmentFromPlayerEnchantment(p).getRarity().getOrder() : 0)).map(playerEnchantment -> playerEnchantmentToString(playerEnchantment)).forEach(
                playerEnchantmentLore -> pickaxeBuilder.addLore(playerEnchantmentLore)
        );

        prisonPlayer.getPlayerEnchantments().stream().forEach(
                playerEnchantment -> applyEnchantments.applyEnchantment(playerEnchantment, pickaxeBuilder)
        );

        pickaxeBuilder.setAllItemFlags();

        pickaxeBuilder.setDamage(0);

        pickaxeBuilder.setUnbreakable(true);

        net.minecraft.world.item.ItemStack item = CraftItemStack.asNMSCopy(pickaxeBuilder.build());
        NBTTagCompound tag = (item.u() ? item.v() : new NBTTagCompound());

        tag.a("PickaxeUUID", UUID.fromString(prisonPlayer.getPlayerUuid()));
        tag.a("PickaxePrisonPlayerId", prisonPlayer.getId());

        item.b(tag);

        return CraftItemStack.asBukkitCopy(item);
    }

    private String playerEnchantmentToString(PlayerEnchantment playerEnchantment) {
        PickaxeUtil.PickaxeEnchantment pickaxeEnchantment = getPickaxeEnchantmentFromPlayerEnchantment(playerEnchantment);

        PickaxeUtil.EnchantmentRarity rarity = pickaxeEnchantment != null ? pickaxeEnchantment.getRarity() : new PickaxeUtil.EnchantmentRarity(0, "Undefined", "§c", 0);

        return rarity.getColorPrefix() + "❚ §7" + playerEnchantment.getEnchantmentName() + " " + playerEnchantment.getEnchantmentLevel();
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
                    inventory.setItem(slot, buildPlayerPickaxe(player));
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
