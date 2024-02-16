package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class CubeEnchantment extends PickaxeEnchantmentImpl {
    public CubeEnchantment() {
        super("Cube");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment cubeEnchantment, BlockBreakEvent e) {
        int level = cubeEnchantment.getEnchantmentLevel();

        PlayerDrops extraPlayerDrops = new PlayerDrops();

        for (int offsetX = -level; offsetX <= level; offsetX++) {
            for (int offsetY = -level; offsetY <= level; offsetY++) {
                for (int offsetZ = -level; offsetZ <= level; offsetZ++) {
                    if (!(offsetX == 0 && offsetY == 0 && offsetZ == 0)) {
                        Block offset = e.getBlock().getRelative(offsetX, offsetY, offsetZ);
                        if (offset.getType().isAir())
                            continue;
                        if (offset.getType().getHardness() == -1.0F)
                            continue;
                        if (offset.isLiquid())
                            continue;

                        offset.setType(Material.AIR);
                        extraPlayerDrops.add(randomPlayerDropsForCube());
                    }
                }
            }
        }
        return extraPlayerDrops;
    }

    public PlayerDrops randomPlayerDropsForCube() {
        // TODO: Genaue Werte anpassen, ggf. aus Config auslesen.
        return PlayerDrops.generate(0, 0, 0, 0, 1, 5);
    }
}
