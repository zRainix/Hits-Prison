package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
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
        MemoryConfiguration defaultConfig = new MemoryConfiguration();
        defaultConfig.set("Enchantment.Efficiency.MaxLevel", 10);
        defaultConfig.set("Enchantment.Efficiency.Type", 1);
        defaultConfig.set("Enchantment.Efficiency.Rarity", 1);
        defaultConfig.set("EnchantmentType.1.Name", "Enchantment");
        defaultConfig.set("EnchantmentRarity.1.Name", "Common");
        defaultConfig.set("EnchantmentRarity.1.ColorPrefix", "§7");
        defaultConfig.set("EnchantmentRarity.1.Order", 1);
        cfg.addDefaults(defaultConfig);
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Enchantment", null);
        for (PickaxeEnchantment enchantment : pickaxeEnchantments) {
            cfg.set("Enchantment." + enchantment.getName() + ".Description", enchantment.getDescription());
            cfg.set("Enchantment." + enchantment.getName() + ".PreviewMaterial", enchantment.getPreviewMaterial().name());
            cfg.set("Enchantment." + enchantment.getName() + ".MaxLevel", enchantment.getMaxLevel());
            cfg.set("Enchantment." + enchantment.getName() + ".Type", enchantment.getType().getId());
            cfg.set("Enchantment." + enchantment.getName() + ".Rarity", enchantment.getRarity().getId());
            for (EnchantmentLevel enchantmentLevel : enchantment.getEnchantmentLevels()) {
                cfg.set("Enchantment." + enchantment.getName() + ".Level." + enchantmentLevel.getLevel() + ".Price", enchantmentLevel.getPrice());
                cfg.set("Enchantment." + enchantment.getName() + ".Level." + enchantmentLevel.getLevel() + ".ActivationChance", enchantmentLevel.getActivationChance());
            }
        }
        cfg.set("EnchantmentType", null);
        for (PickaxeEnchantmentType pickaxeEnchantmentType : pickaxeEnchantmentTypes) {
            cfg.set("EnchantmentType." + pickaxeEnchantmentType.getId() + ".Name", pickaxeEnchantmentType.getName());
        }
        cfg.set("EnchantmentRarity", null);
        for (PickaxeEnchantmentRarity pickaxeEnchantmentRarity : enchantmentRarities) {
            cfg.set("EnchantmentRarity." + pickaxeEnchantmentRarity.getId() + ".Name", pickaxeEnchantmentRarity.getName());
            cfg.set("EnchantmentRarity." + pickaxeEnchantmentRarity.getId() + ".ColorPrefix", pickaxeEnchantmentRarity.getColorPrefix());
            cfg.set("EnchantmentRarity." + pickaxeEnchantmentRarity.getId() + ".Order", pickaxeEnchantmentRarity.getOrder());
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

        for (String id : enchantmentTypeSection.getKeys(false)) {
            int enchantmentTypeId = Integer.parseInt(id);

            String name = enchantmentTypeSection.getString(id + ".Name");
            pickaxeEnchantmentTypes.add(new PickaxeEnchantmentType(enchantmentTypeId, name));
        }
    }

    private void loadEnchantmentRarity() {
        enchantmentRarities.clear();

        ConfigurationSection enchantmentRaritySection = cfg.getConfigurationSection("EnchantmentRarity");

        if (enchantmentRaritySection == null)
            return;

        for (String id : enchantmentRaritySection.getKeys(false)) {
            int enchantmentRarityId = Integer.parseInt(id);
            String name = enchantmentRaritySection.getString(id + ".Name", "Rarity-" + id);
            String colorPrefix = enchantmentRaritySection.getString(id + ".ColorPrefix", "§a");
            int order = enchantmentRaritySection.getInt(id + ".Order", 0);
            enchantmentRarities.add(new PickaxeEnchantmentRarity(enchantmentRarityId, name, colorPrefix, order));
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
            int type = enchantmentSection.getInt(name + ".Type", 1);
            int rarity = enchantmentSection.getInt(name + ".Rarity", 1);
            PickaxeEnchantmentType pickaxeEnchantmentType = getEnchantmentType(type);
            PickaxeEnchantmentRarity pickaxeEnchantmentRarity = getEnchantmentRarity(rarity);
            if (maxLevel < 1) {
                logger.warning("Could not add enchantment " + name + ": maxLevel must be greater or equal to 1.");
                continue;
            }
            if (pickaxeEnchantmentType == null) {
                logger.warning("Could not add enchantment " + name + ": EnchantmentType by id " + type + " not found.");
                continue;
            }
            if (pickaxeEnchantmentRarity == null) {
                logger.warning("Could not add enchantment " + name + ": EnchantmentRarity by id " + rarity + " not found.");
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

    private PickaxeEnchantmentType getEnchantmentType(int id) {
        for (PickaxeEnchantmentType pickaxeEnchantmentType : pickaxeEnchantmentTypes) {
            if (pickaxeEnchantmentType.getId() == id) {
                return pickaxeEnchantmentType;
            }
        }
        return null;
    }

    private PickaxeEnchantmentType getEnchantmentType(String enchantmentName) {
        for (PickaxeEnchantmentType pickaxeEnchantmentType : pickaxeEnchantmentTypes) {
            if (pickaxeEnchantmentType.getName().equalsIgnoreCase(enchantmentName)) {
                return pickaxeEnchantmentType;
            }
        }
        return null;
    }

    private PickaxeEnchantmentRarity getEnchantmentRarity(int id) {
        for (PickaxeEnchantmentRarity pickaxeEnchantmentRarity : enchantmentRarities) {
            if (pickaxeEnchantmentRarity.getId() == id) {
                return pickaxeEnchantmentRarity;
            }
        }
        return null;
    }

    private PickaxeEnchantmentRarity getEnchantmentRarity(String enchantmentName) {
        for (PickaxeEnchantmentRarity pickaxeEnchantmentRarity : enchantmentRarities) {
            if (pickaxeEnchantmentRarity.getName().equalsIgnoreCase(enchantmentName)) {
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

        private int id;
        private String name;

        public PickaxeEnchantmentType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static class PickaxeEnchantmentRarity {

        private int id;
        private String name;
        private String colorPrefix;
        private int order;

        public PickaxeEnchantmentRarity(int id, String name, String colorPrefix, int order) {
            this.id = id;
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

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
