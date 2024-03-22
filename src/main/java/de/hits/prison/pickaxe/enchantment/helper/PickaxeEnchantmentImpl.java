package de.hits.prison.pickaxe.enchantment.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.pickaxe.helper.VanillaEnchantment;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;

import java.math.BigDecimal;
import java.util.Random;

@Component
public class PickaxeEnchantmentImpl {

    @Autowired
    private static PickaxeUtil pickaxeUtil;

    private final String enchantmentName;
    private final Random random;

    protected PickaxeEnchantmentImpl(String enchantmentName) {
        this.enchantmentName = enchantmentName;
        this.random = new Random();
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

    public boolean checkActivationChance(PlayerEnchantment playerEnchantment) {
        PickaxeUtil.PickaxeEnchantment enchantment = pickaxeUtil.getPickaxeEnchantment(playerEnchantment.getEnchantmentName());
        if (enchantment == null)
            return true;

        PickaxeUtil.EnchantmentLevel level = enchantment.getLevel(playerEnchantment.getEnchantmentLevel());
        if (level == null)
            level = enchantment.getLevel(enchantment.getMaxLevel());

        if (level == null)
            return false;

        BigDecimal activationChance = level.getActivationChance();
        if (activationChance.compareTo(BigDecimal.ONE) == 0) {
            return true;
        }

        BigDecimal activation = BigDecimal.valueOf(random.nextDouble());

        return activationChance.compareTo(activation) >= 0;
    }

}
