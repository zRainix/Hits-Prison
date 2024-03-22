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
        NextLevels nextLevels = getNextLevels(enchantment, playerEnchantment);

        ItemBuilder itemBuilder = new ItemBuilder(enchantment.getPreviewMaterial())
                .setAllItemFlags()
                .setDisplayName(enchantment.getFullName())
                .addLoreBreak()
                .addLoreHeading("Description")
                .addLoreWithPrefix("§7", enchantment.getDescription().split("\n"))
                .addLoreBreak();

        if (nextLevels == null || nextLevels.isNotDefined()) {
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

        boolean maxLevel = nextLevels.isMaxLevel();

        itemBuilder
                .addLoreHeading("Stats")
                .addLore("§7Current level: " + (maxLevel ? "§6" : "§b") + playerLevel + "§8/§6" + enchantment.getMaxLevel());

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

        for (int level : nextLevels.getNextLevels()) {
            PickaxeUtil.EnchantmentLevel enchantmentLevel = enchantment.getLevel(level);
            BigInteger price = calculatePrice(enchantment, playerLevel, level);
            String levelTitle = "§7Level §b" + level;
            if (level == 1) {
                levelTitle += " §8(§aActivate Enchantment§8)";
            } else if (level >= enchantment.getMaxLevel()) {
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

    public static NextLevels getNextLevels(PickaxeUtil.PickaxeEnchantment enchantment, PlayerEnchantment playerEnchantment) {
        int maxLevel = enchantment.getMaxLevel();

        if (playerEnchantment != null && playerEnchantment.getEnchantmentLevel() >= maxLevel)
            return null;

        if (enchantment.getEnchantmentLevels().isEmpty()) {
            return new NextLevels(List.of(), false, false, true);
        }

        if (playerEnchantment == null) {
            return new NextLevels(List.of(1), true, false, false);
        }

        int playerLevel = playerEnchantment.getEnchantmentLevel();

        if (playerLevel >= enchantment.getMaxLevel()) {
            return new NextLevels(List.of(), false, true, false);
        }

        PrisonPlayer prisonPlayer = playerEnchantment.getRefPrisonPlayer();

        List<Integer> levels = new ArrayList<>();
        int plusOne = playerLevel + 1;
        if (possibleToBuyLevel(prisonPlayer, enchantment, playerLevel, plusOne))
            levels.add(plusOne);

        int plusTen = playerLevel + 10;
        if (possibleToBuyLevel(prisonPlayer, enchantment, playerLevel, plusTen))
            levels.add(plusTen);

        int maxPurchasableLevel = -1;
        for (int level = playerLevel + 2; level <= maxLevel; level++) {
            if (!possibleToBuyLevel(prisonPlayer, enchantment, playerLevel, level)) {
                break;
            }
            maxPurchasableLevel = level;
        }
        if (maxPurchasableLevel != -1 && !levels.contains(maxPurchasableLevel))
            levels.add(maxPurchasableLevel);

        return new NextLevels(levels, false, false, false);
    }

    public static class NextLevels {

        List<Integer> nextLevels;
        boolean activation, maxLevel, notDefined;

        public NextLevels(List<Integer> nextLevels, boolean activation, boolean maxLevel, boolean notDefined) {
            this.nextLevels = nextLevels;
            this.activation = activation;
            this.maxLevel = maxLevel;
            this.notDefined = notDefined;
        }

        public List<Integer> getNextLevels() {
            return nextLevels;
        }

        public void setNextLevels(List<Integer> nextLevels) {
            this.nextLevels = nextLevels;
        }

        public boolean isActivation() {
            return activation;
        }

        public void setActivation(boolean activation) {
            this.activation = activation;
        }

        public boolean isMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(boolean maxLevel) {
            this.maxLevel = maxLevel;
        }

        public boolean isNotDefined() {
            return notDefined;
        }

        public void setNotDefined(boolean notDefined) {
            this.notDefined = notDefined;
        }
    }

    public static boolean possibleToBuyLevel(PrisonPlayer prisonPlayer, PickaxeUtil.PickaxeEnchantment enchantment, int playerLevel, int level) {
        if (enchantment.getLevel(level) == null) {
            return false;
        }

        BigInteger playerBalance = prisonPlayer.getPlayerCurrency().getObsidianShards();
        BigInteger price = calculatePrice(enchantment, playerLevel, level);

        if (playerBalance.compareTo(price) < 0) {
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
        } else if (level >= enchantment.getMaxLevel()) {
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
