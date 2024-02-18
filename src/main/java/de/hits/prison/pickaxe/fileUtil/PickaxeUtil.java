package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

@SettingsFile
public class PickaxeUtil extends FileUtil {

    private final Logger logger = Bukkit.getLogger();

    List<PickaxeEnchantment> pickaxeEnchantments;
    List<PickaxeEnchantmentType> pickaxeEnchantmentTypes;
    List<PickaxeEnchantmentRarity> enchantmentRarities;

    public PickaxeUtil() {
        super("pickaxe.yml");
        pickaxeEnchantments = new ArrayList<>();
        pickaxeEnchantmentTypes = new ArrayList<>();
        enchantmentRarities = new ArrayList<>();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Enchantment", null);
        for (PickaxeEnchantment enchantment : pickaxeEnchantments) {
            cfg.set("Enchantment." + enchantment.getName() + ".Description", enchantment.getDescription());
            cfg.set("Enchantment." + enchantment.getName() + ".PreviewMaterial", enchantment.getPreviewMaterial().name());
            cfg.set("Enchantment." + enchantment.getName() + ".MaxLevel", enchantment.getMaxLevel());
            cfg.set("Enchantment." + enchantment.getName() + ".Type", enchantment.getType().getName());
            cfg.set("Enchantment." + enchantment.getName() + ".Rarity", enchantment.getRarity().getName());
            for (EnchantmentLevel enchantmentLevel : enchantment.getEnchantmentLevels()) {
                cfg.set("Enchantment." + enchantment.getName() + ".Level." + enchantmentLevel.getLevel() + ".Price", enchantmentLevel.getPrice().toString());
                cfg.set("Enchantment." + enchantment.getName() + ".Level." + enchantmentLevel.getLevel() + ".ActivationChance", enchantmentLevel.getActivationChance().toString());
            }
        }
        cfg.set("EnchantmentType", null);
        for (PickaxeEnchantmentType pickaxeEnchantmentType : pickaxeEnchantmentTypes) {
            cfg.set("EnchantmentType." + pickaxeEnchantmentType.getName() + ".PreviewMaterial", pickaxeEnchantmentType.getPreviewMaterial().name());
        }
        cfg.set("EnchantmentRarity", null);
        for (PickaxeEnchantmentRarity pickaxeEnchantmentRarity : enchantmentRarities) {
            cfg.set("EnchantmentRarity." + pickaxeEnchantmentRarity.getName() + ".ColorPrefix", pickaxeEnchantmentRarity.getColorPrefix());
            cfg.set("EnchantmentRarity." + pickaxeEnchantmentRarity.getName() + ".Order", pickaxeEnchantmentRarity.getOrder());
        }
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();

        loadEnchantmentType();
        loadEnchantmentRarity();
        loadEnchantment();
    }

    private void loadEnchantmentType() {
        pickaxeEnchantmentTypes.clear();

        ConfigurationSection enchantmentTypeSection = cfg.getConfigurationSection("EnchantmentType");

        if (enchantmentTypeSection == null)
            return;

        for (String name : enchantmentTypeSection.getKeys(false)) {
            Material previewMaterial = Material.getMaterial(enchantmentTypeSection.getString(name + ".PreviewMaterial", "DIAMOND_PICKAXE").toUpperCase());
            pickaxeEnchantmentTypes.add(new PickaxeEnchantmentType(name, previewMaterial));
        }
    }

    private void loadEnchantmentRarity() {
        enchantmentRarities.clear();

        ConfigurationSection enchantmentRaritySection = cfg.getConfigurationSection("EnchantmentRarity");

        if (enchantmentRaritySection == null)
            return;

        for (String name : enchantmentRaritySection.getKeys(false)) {
            String colorPrefix = enchantmentRaritySection.getString(name + ".ColorPrefix", "§a");
            int order = enchantmentRaritySection.getInt(name + ".Order", 0);
            enchantmentRarities.add(new PickaxeEnchantmentRarity(name, colorPrefix, order));
        }
    }

    private void loadEnchantment() {
        pickaxeEnchantments.clear();

        ConfigurationSection enchantmentSection = cfg.getConfigurationSection("Enchantment");

        if (enchantmentSection == null)
            return;

        for (String name : enchantmentSection.getKeys(false)) {
            String description = enchantmentSection.getString(name + ".Description", "");
            Material previewMaterial = Material.getMaterial(enchantmentSection.getString(name + ".PreviewMaterial", "DIAMOND").toUpperCase());
            int maxLevel = enchantmentSection.getInt(name + ".MaxLevel", 0);
            String type = enchantmentSection.getString(name + ".Type", "None");
            String rarity = enchantmentSection.getString(name + ".Rarity", "Common");
            PickaxeEnchantmentType pickaxeEnchantmentType = getEnchantmentType(type);
            PickaxeEnchantmentRarity pickaxeEnchantmentRarity = getEnchantmentRarity(rarity);
            if (maxLevel < 1) {
                logger.warning("Could not add enchantment " + name + ": maxLevel must be greater or equal to 1.");
                continue;
            }
            if (pickaxeEnchantmentType == null) {
                logger.warning("Could not add enchantment " + name + ": EnchantmentType by name " + type + " not found.");
                continue;
            }
            if (pickaxeEnchantmentRarity == null) {
                logger.warning("Could not add enchantment " + name + ": EnchantmentRarity by name " + rarity + " not found.");
                continue;
            }
            List<EnchantmentLevel> enchantmentLevels = new ArrayList<>();
            ConfigurationSection levelSection = enchantmentSection.getConfigurationSection(name + ".Level");
            if (levelSection != null) {
                for (String levelString : levelSection.getKeys(false)) {
                    int level = Integer.parseInt(levelString);
                    BigInteger price = new BigInteger(levelSection.getString(levelString + ".Price", "0"));
                    BigDecimal activationChance = new BigDecimal(levelSection.getString(levelString + ".ActivationChance", "1.0"));
                    enchantmentLevels.add(new EnchantmentLevel(level, price, activationChance));
                }
            }
            enchantmentLevels.sort(Comparator.comparingInt(EnchantmentLevel::getLevel));
            boolean notFound = false;
            for (int i = 0; i < enchantmentLevels.size(); i++) {
                if (enchantmentLevels.get(i).getLevel() != i + 1) {
                    logger.warning("Could not add enchantment " + name + ": Level " + (i + 1) + " not found.");
                    notFound = true;
                    break;
                }
            }
            if (notFound)
                continue;

            pickaxeEnchantments.add(new PickaxeEnchantment(name, description, previewMaterial, maxLevel, pickaxeEnchantmentType, pickaxeEnchantmentRarity, enchantmentLevels));
        }
    }

    public PickaxeEnchantment getPickaxeEnchantment(String name) {
        for (PickaxeEnchantment enchantment : pickaxeEnchantments) {
            if (enchantment.getName().equalsIgnoreCase(name)) {
                return enchantment;
            }
        }
        return null;
    }

    public void setPickaxeEnchantment(PickaxeEnchantment pickaxeEnchantment) {
        pickaxeEnchantments.removeIf(enchantment -> enchantment.getName().equals(pickaxeEnchantment.getName()));
        pickaxeEnchantments.add(pickaxeEnchantment);
    }

    public PickaxeEnchantmentType getEnchantmentType(String name) {
        for (PickaxeEnchantmentType pickaxeEnchantmentType : pickaxeEnchantmentTypes) {
            if (pickaxeEnchantmentType.getName().equalsIgnoreCase(name)) {
                return pickaxeEnchantmentType;
            }
        }
        return null;
    }

    public PickaxeEnchantmentRarity getEnchantmentRarity(String name) {
        for (PickaxeEnchantmentRarity pickaxeEnchantmentRarity : enchantmentRarities) {
            if (pickaxeEnchantmentRarity.getName().equalsIgnoreCase(name)) {
                return pickaxeEnchantmentRarity;
            }
        }
        return null;
    }

    public List<PickaxeEnchantment> getPickaxeEnchantments() {
        return pickaxeEnchantments;
    }

    public List<PickaxeEnchantmentType> getPickaxeEnchantmentTypes() {
        return pickaxeEnchantmentTypes;
    }

    public List<PickaxeEnchantmentRarity> getEnchantmentRarities() {
        return enchantmentRarities;
    }

    public static class PickaxeEnchantment {

        private String name;
        private String description;
        private Material previewMaterial;
        private int maxLevel;
        private PickaxeEnchantmentType type;
        private PickaxeEnchantmentRarity rarity;
        private List<EnchantmentLevel> enchantmentLevels;

        public PickaxeEnchantment(String name, String description, Material previewMaterial, int maxLevel, PickaxeEnchantmentType type, PickaxeEnchantmentRarity rarity, List<EnchantmentLevel> enchantmentLevels) {
            this.name = name;
            this.description = description;
            this.previewMaterial = previewMaterial;
            this.maxLevel = maxLevel;
            this.type = type;
            this.rarity = rarity;
            this.enchantmentLevels = enchantmentLevels;
        }

        public String getName() {
            return name;
        }

        public String getFullName() {
            return "§8[§r" + rarity.getColorPrefix() + rarity.getName() + "§8] §7" + getName();
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Material getPreviewMaterial() {
            return previewMaterial;
        }

        public void setPreviewMaterial(Material previewMaterial) {
            this.previewMaterial = previewMaterial;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public PickaxeEnchantmentType getType() {
            return type;
        }

        public void setType(PickaxeEnchantmentType type) {
            this.type = type;
        }

        public PickaxeEnchantmentRarity getRarity() {
            return rarity;
        }

        public void setRarity(PickaxeEnchantmentRarity rarity) {
            this.rarity = rarity;
        }

        public List<EnchantmentLevel> getEnchantmentLevels() {
            return enchantmentLevels;
        }

        public void setEnchantmentLevels(List<EnchantmentLevel> enchantmentLevels) {
            this.enchantmentLevels = enchantmentLevels;
        }

        public EnchantmentLevel getLevel(int level) {
            for (EnchantmentLevel enchantmentLevel : getEnchantmentLevels()) {
                if (enchantmentLevel.getLevel() == level)
                    return enchantmentLevel;
            }
            return null;
        }
    }

    public static class PickaxeEnchantmentType {

        private String name;
        private Material previewMaterial;

        public PickaxeEnchantmentType(String name, Material previewMaterial) {
            this.name = name;
            this.previewMaterial = previewMaterial;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Material getPreviewMaterial() {
            return previewMaterial;
        }

        public void setPreviewMaterial(Material previewMaterial) {
            this.previewMaterial = previewMaterial;
        }
    }

    public static class PickaxeEnchantmentRarity {

        private String name;
        private String colorPrefix;
        private int order;

        public PickaxeEnchantmentRarity(String name, String colorPrefix, int order) {
            this.name = name;
            this.colorPrefix = colorPrefix;
            this.order = order;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColorPrefix() {
            return colorPrefix;
        }

        public void setColorPrefix(String colorPrefix) {
            this.colorPrefix = colorPrefix;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }

    public static class EnchantmentLevel {

        private int level;
        private BigInteger price;
        private BigDecimal activationChance;

        public EnchantmentLevel(int level, BigInteger price, BigDecimal activationChance) {
            this.level = level;
            this.price = price;
            this.activationChance = activationChance;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public BigInteger getPrice() {
            return price;
        }

        public void setPrice(BigInteger price) {
            this.price = price;
        }

        public BigDecimal getActivationChance() {
            return activationChance;
        }

        public void setActivationChance(BigDecimal activationChance) {
            this.activationChance = activationChance;
        }
    }
}
