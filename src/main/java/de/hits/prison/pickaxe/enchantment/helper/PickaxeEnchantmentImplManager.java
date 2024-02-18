package de.hits.prison.pickaxe.enchantment.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.base.model.helper.ClassScanner;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PickaxeEnchantmentImplManager implements Manager {

    @Autowired
    private static PickaxeUtil pickaxeUtil;

    private final Set<PickaxeEnchantmentImpl> enchantmentsImplementations;

    public PickaxeEnchantmentImplManager() {
        enchantmentsImplementations = ClassScanner
                .getClassesBySuperclass(ClassScanner.getPackageNameOfParallelPackage(getClass().getPackageName(), "impl"), PickaxeEnchantmentImpl.class)
                .stream().map(clazz -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        for (PickaxeEnchantmentImpl pickaxeEnchantmentImpl : enchantmentsImplementations) {
            if (!pickaxeEnchantmentImpl.getClass().isAnnotationPresent(DefaultEnchantment.class))
                continue;
            String name = pickaxeEnchantmentImpl.getEnchantmentName();
            DefaultEnchantment defaultEnchantment = pickaxeEnchantmentImpl.getClass().getAnnotation(DefaultEnchantment.class);
            generateDefaultEnchantmentConfig(name, defaultEnchantment);
        }
        pickaxeUtil.save();
    }

    private void generateDefaultEnchantmentConfig(String name, DefaultEnchantment defaultEnchantment) {
        String description = defaultEnchantment.description();
        Material previewMaterial = defaultEnchantment.previewMaterial();
        int maxLevel = defaultEnchantment.maxLevel();
        String type = defaultEnchantment.type();
        String rarity = defaultEnchantment.rarity();
        PickaxeUtil.PickaxeEnchantmentType enchantmentType = pickaxeUtil.getEnchantmentType(type);
        if (enchantmentType == null) {
            enchantmentType = new PickaxeUtil.PickaxeEnchantmentType(type, Material.DIAMOND_PICKAXE);
            pickaxeUtil.getPickaxeEnchantmentTypes().add(enchantmentType);
        }
        PickaxeUtil.PickaxeEnchantmentRarity enchantmentRarity = pickaxeUtil.getEnchantmentRarity(rarity);
        if (enchantmentRarity == null) {
            enchantmentRarity = new PickaxeUtil.PickaxeEnchantmentRarity(rarity, "§7", 0);
            pickaxeUtil.getEnchantmentRarities().add(enchantmentRarity);
        }
        PickaxeUtil.PickaxeEnchantment enchantment = pickaxeUtil.getPickaxeEnchantment(name);
        if (enchantment == null) {
            enchantment = new PickaxeUtil.PickaxeEnchantment(name, description, previewMaterial, maxLevel, enchantmentType, enchantmentRarity, new ArrayList<>());
        }
        for (int level = 1; level <= defaultEnchantment.maxLevel(); level++) {
            List<PickaxeUtil.EnchantmentLevel> enchantmentLevels = enchantment.getEnchantmentLevels();
            if (enchantment.getLevel(level) == null) {
                BigInteger price = new BigInteger(defaultEnchantment.activationPrice());
                BigDecimal multiplier = new BigDecimal(defaultEnchantment.priceMultiplier()).pow(level);
                price = new BigDecimal(price).multiply(multiplier).toBigInteger();
                enchantmentLevels.add(new PickaxeUtil.EnchantmentLevel(level, price, BigDecimal.ONE));
            }
        }
        pickaxeUtil.setPickaxeEnchantment(enchantment);
    }


    public Set<PickaxeEnchantmentImpl> getEnchantmentsImplementations() {
        return enchantmentsImplementations;
    }
}
