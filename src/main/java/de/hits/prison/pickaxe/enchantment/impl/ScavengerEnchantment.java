package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import org.bukkit.event.block.BlockBreakEvent;

@Component
@DefaultEnchantment(maxLevel = 1000, description = "Possibility to find Enchantments", activationPrice = "50000", priceMultiplier = "1.1", type = "Effect")
public class ScavengerEnchantment extends PickaxeEnchantmentImpl {

    @Autowired
    private static PickaxeUtil pickaxeUtil;

    public ScavengerEnchantment() {
        super("Scavenger");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment scavengerEnchantment, MineWorld mineWorld, BlockBreakEvent event) {
        int enchantmentLevel = scavengerEnchantment.getEnchantmentLevel();



        return null;
    }
}
