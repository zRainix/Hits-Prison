package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.event.block.BlockBreakEvent;

@DefaultEnchantment(maxLevel = 20, description = "Increases drops from mined block.", activationPrice = "100", priceMultiplier = "2", type = "Drops")
public class FortuneEnchantment extends PickaxeEnchantmentImpl {

    public FortuneEnchantment() {
        super("Fortune");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment fortuneEnchantment, BlockBreakEvent e) {
        playerDrops.multiply(multiplierByLevel(fortuneEnchantment.getEnchantmentLevel()));
        return playerDrops;
    }

    public double multiplierByLevel(int level) {
        return Math.log10((((double) level) / 2D) + 1D) * 2 + ((double) level / 10D);
    }

}
