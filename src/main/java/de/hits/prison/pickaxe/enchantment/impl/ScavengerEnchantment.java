package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import org.bukkit.event.block.BlockBreakEvent;

public class ScavengerEnchantment extends PickaxeEnchantmentImpl {
    public ScavengerEnchantment() {
        super("Scavenger");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment cellsGivingEnchantment, MineWorld mineWorld, BlockBreakEvent event) {



        return null;
    }
}
