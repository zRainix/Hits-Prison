package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.fileUtil.BlockValueUtil;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

@Component
@DefaultEnchantment(maxLevel = 100, description = "Possibility to mine an entire layer", activationPrice = "25000", priceMultiplier = "1.1", type = "Drops")
public class JackhammerEnchantment extends PickaxeEnchantmentImpl {

    @Autowired
    private static BlockValueUtil blockValueUtil;

    public JackhammerEnchantment() {
        super("Jackhammer");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment jackhammerEnchantment, MineWorld mineWorld, BlockBreakEvent event) {
        if (!checkActivationChance(jackhammerEnchantment)) return null;

        Block block = event.getBlock();

        int blockX = block.getX();
        int blockY = block.getY();
        int blockZ = block.getZ();

        int[] bounds = mineWorld.getMineBounds();

        int startX = bounds[0];
        int startZ = bounds[2];
        int endX = bounds[3];
        int endZ = bounds[5];

        PlayerDrops extraDrops = new PlayerDrops();

        for (int x = startX; x <= endX; x++) {
            for (int z = startZ; z <= endZ; z++) {
                if (x == blockX && z == blockZ)
                    continue;

                Block jackhammerBlock = block.getWorld().getBlockAt(x, blockY, z);

                if (!mineWorld.isMineBlock(jackhammerBlock))
                    continue;
                if (jackhammerBlock.getType() == Material.AIR)
                    continue;

                jackhammerBlock.setType(Material.AIR);


                Material material = jackhammerBlock.getType();

                jackhammerBlock.setType(Material.AIR);

                extraDrops.add(PlayerDrops.generate(blockValueUtil.getBlockValue(material)));
            }
        }
        return extraDrops;
    }
}
