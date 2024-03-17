package de.hits.prison.pickaxe.enchantment.helper;

import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.pickaxe.helper.VanillaEnchantment;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

public class PickaxeEnchantmentImpl {

    private final String enchantmentName;

    protected PickaxeEnchantmentImpl(String enchantmentName) {
        this.enchantmentName = enchantmentName;
    }

    public String getEnchantmentName() {
        return enchantmentName;
    }

    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment playerEnchantment, MineWorld mineWorld, BlockBreakEvent event) {
        return null;
    }

    public VanillaEnchantment getVanillaEnchantment(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment) {
        return null;
    }

    public PotionEffect getVanillaPotionEffect(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment) {
        return null;
    }

    public void onRightClickAir(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment, MineWorld mineWorld, PlayerInteractEvent event) {
    }

    public void onRightClickEntity(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment, MineWorld mineWorld, PlayerInteractAtEntityEvent event) {
    }

    public void onRightClickBlock(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment, MineWorld mineWorld, PlayerInteractEvent event) {
    }

}
