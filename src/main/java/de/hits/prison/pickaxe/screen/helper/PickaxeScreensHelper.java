package de.hits.prison.pickaxe.screen.helper;

import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
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
                .addLoreHeading("Description")
                .addLoreWithPrefix("§7", enchantment.getDescription().split("\n"))
                .addLoreBreak();

        if (nextLevels != null && nextLevels.isEmpty()) {
            return itemBuilder.build();
        }

        int playerLevel = playerEnchantment != null ? playerEnchantment.getEnchantmentLevel() : 0;

        if (playerLevel == 0) {
            itemBuilder
                    .addLoreHeading("Activation")
                    .addLore("§7Price to activate: §b" + NumberUtil.formatValue(enchantment.getLevel(1).getPrice()))
                    .addLoreBreak();
            return itemBuilder.build();
        }

        boolean maxLevel = nextLevels == null;

        itemBuilder
                .addLoreHeading("Stats")
                .addLore("§7Current level: §b" + playerLevel + (maxLevel ? " §8(§6Max§8)" : ""));

        if (enchantment.getLevel(1).getActivationChance().compareTo(BigDecimal.valueOf(1D)) != 0) {
            PickaxeUtil.EnchantmentLevel enchantmentLevel = enchantment.getLevel(playerLevel);
            itemBuilder
                    .addLore("§7Activation chance: §b" + enchantmentLevel.getActivationChance().multiply(BigDecimal.valueOf(100)) + "%");
        }

        itemBuilder
                .addLoreBreak();

        if (maxLevel)
            return itemBuilder.build();

        itemBuilder
                .addLoreHeading("Next levels");

        for (int level : nextLevels) {
            PickaxeUtil.EnchantmentLevel enchantmentLevel = enchantment.getLevel(level);
            BigInteger price = calculatePrice(enchantment, playerLevel, level);
            String levelTitle = "§7Level §b" + level;
            if (level == 1) {
                levelTitle += " §8(§aActivate Enchantment§8)";
            } else if (level == enchantment.getMaxLevel()) {
                levelTitle += " §8(§6Max§8)";
            }
            levelTitle += "§7:";
            itemBuilder.addLore(levelTitle)
                    .addLore("§7- Price: §b" + NumberUtil.formatValue(price));
            if (enchantment.getLevel(1).getActivationChance().compareTo(BigDecimal.valueOf(1D)) != 0) {
                itemBuilder
                        .addLore("§7- Activation chance: §b" + enchantmentLevel.getActivationChance().multiply(BigDecimal.valueOf(100)) + "%");
            }
            itemBuilder.addLoreBreak();
        }

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
     * else returns player level + 1, player level + 10 (if possible) and max purchasable level
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
        PrisonPlayer prisonPlayer = playerEnchantment.getRefPrisonPlayer();

        List<Integer> levels = new ArrayList<>();
        int plusOne = playerLevel + 1;
        if (possibleToBuyLevel(prisonPlayer, enchantment, playerLevel, plusOne))
            levels.add(plusOne);

        int plusTen = playerLevel + 10;
        if (possibleToBuyLevel(prisonPlayer, enchantment, playerLevel, plusTen))
            levels.add(plusTen);

        int maxPurchasableLevel = -1;
        for(int level = playerLevel+2; level <= maxLevel; level++) {
            if(!possibleToBuyLevel(prisonPlayer, enchantment, playerLevel, level)) {
                break;
            }
            maxPurchasableLevel = level;
        }
        if(maxPurchasableLevel != -1 && !levels.contains(maxPurchasableLevel))
            levels.add(maxPurchasableLevel);

        return levels;
    }

    public static boolean possibleToBuyLevel(PrisonPlayer prisonPlayer, PickaxeUtil.PickaxeEnchantment enchantment, int playerLevel, int level) {
        if(enchantment.getLevel(level) == null) {
            return false;
        }

        BigInteger playerBalance = prisonPlayer.getPlayerCurrency().getVolcanicAsh();
        BigInteger price = calculatePrice(enchantment, playerLevel, level);

        if(playerBalance.compareTo(price) < 0) {
            return false;
        }
        return true;
    }

    public static BigInteger calculatePrice(PickaxeUtil.PickaxeEnchantment enchantment, int currentLevel, int targetLevel) {
        BigInteger price = BigInteger.valueOf(0);
        for (int i = currentLevel; i < targetLevel; i++) {
            price = price.add(enchantment.getLevel(i + 1).getPrice());
        }
        return price;
    }

    public static ItemStack buildLevelItem(PickaxeUtil.PickaxeEnchantment enchantment, PlayerEnchantment playerEnchantment, int level) {
        int playerLevel = playerEnchantment != null ? playerEnchantment.getEnchantmentLevel() : 0;
        PickaxeUtil.EnchantmentLevel enchantmentLevel = enchantment.getLevel(level);
        if (enchantmentLevel == null) {
            return null;
        }

        BigInteger price = calculatePrice(enchantment, playerLevel, level);

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
