package de.hits.prison.pickaxe.screen.helper;

import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.server.util.ItemBuilder;
import de.hits.prison.server.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PickaxeScreensHelper {

    public static ItemStack buildEnchantmentItem(PickaxeUtil.PickaxeEnchantment enchantment, PlayerEnchantment playerEnchantment) {
        List<Integer> nextLevels = getNextLevels(enchantment, playerEnchantment);

        ItemBuilder itemBuilder = new ItemBuilder(enchantment.getPreviewMaterial())
                .setAllItemFlags()
                .setDisplayName(enchantment.getFullName())
                .addLoreBreak()
                .addLore("§8-- §bDescription §8--")
                .addLore("§7" + enchantment.getDescription())
                .addLoreBreak();
        if (nextLevels == null || nextLevels.isEmpty()) {
            itemBuilder
                    .addLore("§6§lMax Level")
                    .addLoreBreak();
            return itemBuilder.build();
        }
        /*
        TODO: Add levels
        - Use PickaxeScreensHelper#getNextLevels to get next levels to display. Use methods java doc for help.
        - if first level: different text: purchase instead of upgrade
        - if first level has activation chance of 1 do not display activation chance
         */
        return itemBuilder.build();
    }

    /**
     * Returns next levels to buy for given enchantment
     *
     * @param enchantment       pickaxe enchantment to get levels from
     * @param playerEnchantment matching player enchantment
     * @return list of next levels for player to buy
     * if enchantment has no levels: empty list
     * if player enchantment is null: only first level (buy)
     * if player enchantment level is enchantment max level: returns null
     * else returns player level + 1, player level + 10 (if possible) and enchantment max level
     */
    public static List<Integer> getNextLevels(PickaxeUtil.PickaxeEnchantment enchantment, PlayerEnchantment playerEnchantment) {
        int maxLevel = enchantment.getMaxLevel();

        if (playerEnchantment != null && playerEnchantment.getEnchantmentLevel() >= maxLevel)
            return null;

        if (enchantment.getEnchantmentLevels().isEmpty())
            return List.of();

        if (playerEnchantment == null)
            return List.of(1);

        int playerLevel = playerEnchantment.getEnchantmentLevel();

        List<Integer> levels = new ArrayList<>();
        int plusOne = playerLevel + 1;
        levels.add(plusOne);

        int plusTen = playerLevel + 10;
        if (enchantment.getLevel(10) != null)
            levels.add(plusTen);

        if (plusOne != maxLevel && plusTen != maxLevel && enchantment.getLevel(maxLevel) != null)
            levels.add(maxLevel);

        return levels;
    }

    public static ItemStack buildLevelItem(PickaxeUtil.PickaxeEnchantment enchantment, PlayerEnchantment playerEnchantment, int level) {
        int playerLevel = playerEnchantment != null ? playerEnchantment.getEnchantmentLevel() : 0;
        PickaxeUtil.EnchantmentLevel enchantmentLevel = enchantment.getLevel(level);
        if (enchantmentLevel == null) {
            return null;
        }
        BigInteger price = BigInteger.valueOf(0);
        for (int i = playerLevel; i < level; i++) {
            price = price.add(enchantment.getLevel(i + 1).getPrice());
        }

        String displayName = "§8Buy Level: §b" + level;
        if (level == 1) {
            displayName += " §8(§aActivate Enchantment§8)";
        } else if (level == enchantment.getMaxLevel()) {
            displayName += " §8(§6Max§8)";
        }

        ItemBuilder itemBuilder = new ItemBuilder(Material.EMERALD);
        itemBuilder
                .setAllItemFlags()
                .setDisplayName(displayName)
                .addLoreBreak()
                .addLore("§7Price: §b" + NumberUtil.formatValue(price))
                .addLoreBreak();
        if (enchantment.getLevel(1).getActivationChance().compareTo(BigDecimal.valueOf(1D)) != 0) {
            itemBuilder
                    .addLore("§7Activation chance: §b" + enchantmentLevel.getActivationChance().multiply(BigDecimal.valueOf(100)) + "%")
                    .addLoreBreak();
        }
        return itemBuilder.build();
    }
}
