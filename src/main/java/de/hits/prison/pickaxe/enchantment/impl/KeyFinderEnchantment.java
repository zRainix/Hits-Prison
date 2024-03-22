package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.fileUtil.KeyFinderUtil;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.server.util.ItemBuilder;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Random;

@Component
@DefaultEnchantment(maxLevel = 10, description = "Possibility to find Keys", activationPrice = "250", priceMultiplier = "1.3", type = "Drops")
public class KeyFinderEnchantment extends PickaxeEnchantmentImpl {


    @Autowired
    private static KeyFinderUtil keyFinderUtil;
    @Autowired
    private static PickaxeUtil pickaxeUtil;

    private final Random random;

    public KeyFinderEnchantment() {
        super("KeyFinder");
        this.random = new Random();
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment keyFinderEnchantment, MineWorld mineWorld, BlockBreakEvent event) {
        PickaxeUtil.PickaxeEnchantment pickaxeEnchantment = pickaxeUtil.getPickaxeEnchantment(keyFinderEnchantment.getEnchantmentName());
        if(pickaxeEnchantment == null) {
            return null;
        }

        KeyFinderUtil.KeyFinderLevel keyFinderLevel = keyFinderUtil.getKeyFinderLevel(keyFinderEnchantment.getEnchantmentLevel());
        if(keyFinderLevel == null) {
            return null;
        }

        for(KeyFinderUtil.KeyFinderLevelDrop drop : keyFinderLevel.getDrops()) {
            BigDecimal chance = drop.getChance();
            BigDecimal randomNumber = BigDecimal.valueOf(random.nextFloat());
            if(randomNumber.compareTo(chance) >= 0) {
                continue;
            }
            ItemStack itemStack = new ItemBuilder(Material.NAME_TAG).setDisplayName(drop.getDisplayName()).build();
            event.getPlayer().getInventory().addItem(itemStack);
            MessageUtil.sendMessage(event.getPlayer(), "§7you found " + drop.getDisplayName());
        }

        return null;
    }
}
