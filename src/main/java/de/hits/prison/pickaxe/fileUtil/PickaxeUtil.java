package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.server.fileUtil.anno.SettingsFile;
import de.hits.prison.server.fileUtil.helper.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SettingsFile
public class PickaxeUtil extends FileUtil {

    private Logger logger = Bukkit.getLogger();

    List<PickaxeEnchantment> pickaxeEnchantments;
    List<EnchantmentType> enchantmentTypes;
    List<EnchantmentRarity> enchantmentRarities;

    public PickaxeUtil() {
        super("pickaxe.yml");
        pickaxeEnchantments = new ArrayList<>();
        enchantmentTypes = new ArrayList<>();
        enchantmentRarities = new ArrayList<>();
    }

    @Override
    public void init() {
        MemoryConfiguration defaultConfig = new MemoryConfiguration();
        defaultConfig.set("Enchantment.Efficiency.MaxLevel", 10);
        defaultConfig.set("Enchantment.Efficiency.Type", 1);
        cfg.addDefaults(defaultConfig);
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Enchantment", null);
        for (PickaxeEnchantment enchantment : pickaxeEnchantments) {
            cfg.set("Enchantment." + enchantment.getName() + ".MaxLevel", enchantment.getMaxLevel());
            cfg.set("Enchantment." + enchantment.getName() + ".Type", enchantment.getRarity().getId());
            cfg.set("Enchantment." + enchantment.getName() + ".Rarity", enchantment.getType().getId());
        }
        cfg.set("EnchantmentType", null);
        for (EnchantmentType enchantmentType : enchantmentTypes) {
            cfg.set("EnchantmentType." + enchantmentType.getId() + ".Name", enchantmentType.getName());
        }
        cfg.set("EnchantmentRarity", null);
        for (EnchantmentRarity enchantmentRarity : enchantmentRarities) {
            cfg.set("EnchantmentRarity." + enchantmentRarity.getId() + ".Name", enchantmentRarity.getName());
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
        if (!cfg.contains("EnchantmentType")) {
            return;
        }
        ConfigurationSection enchantmentTypeSection = cfg.getConfigurationSection("EnchantmentType");
        for (String id : enchantmentTypeSection.getKeys(false)) {
            int enchantmentTypeId = Integer.parseInt(id);

            String name = enchantmentTypeSection.getString(id + ".Name");
            enchantmentTypes.add(new EnchantmentType(enchantmentTypeId, name));
        }
    }

    private void loadEnchantmentRarity() {
        if (!cfg.contains("EnchantmentRarity")) {
            return;
        }
        ConfigurationSection enchantmentRaritySection = cfg.getConfigurationSection("EnchantmentRarity");
        for (String id : enchantmentRaritySection.getKeys(false)) {
            int enchantmentRarityId = Integer.parseInt(id);
            String name = enchantmentRaritySection.getString(id + ".Name", "Rarity-" + id);
            String colorPrefix = enchantmentRaritySection.getString(id + ".ColorPrefix", "§a");
            int order = enchantmentRaritySection.getInt(id + ".ColorPrefix", 0);
            enchantmentRarities.add(new EnchantmentRarity(enchantmentRarityId, name, colorPrefix, order));
        }
    }

    private void loadEnchantment() {
        if (!cfg.contains("Enchantment")) {
            return;
        }
        ConfigurationSection enchantmentSection = cfg.getConfigurationSection("Enchantment");
        for (String name : enchantmentSection.getKeys(false)) {
            int maxLevel = enchantmentSection.getInt(name + ".MaxLevel", 0);
            String type = enchantmentSection.getString(name + ".Type", "");
            String rarity = enchantmentSection.getString(name + ".Rarity", "");
            EnchantmentType enchantmentType = getEnchantmentType(type);
            EnchantmentRarity enchantmentRarity = getEnchantmentRarity(rarity);
            if (maxLevel < 1) {
                logger.warning("Could not add enchantment " + name + ": maxLevel must be greater or equal to 1.");
                continue;
            }
            if (enchantmentType == null) {
                logger.warning("Could not add enchantment " + name + ": EnchantmentType by id " + type + " not found.");
                continue;
            }
            if (enchantmentRarity == null) {
                logger.warning("Could not add enchantment " + name + ": EnchantmentRarity by id " + rarity + " not found.");
                continue;
            }
            pickaxeEnchantments.add(new PickaxeEnchantment(name, maxLevel, enchantmentType, enchantmentRarity));
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

    private EnchantmentType getEnchantmentType(String enchantmentName) {
        for (EnchantmentType enchantmentType : enchantmentTypes) {
            if (enchantmentType.getName().equalsIgnoreCase(enchantmentName)) {
                return enchantmentType;
            }
        }
        return null;
    }

    private EnchantmentRarity getEnchantmentRarity(String enchantmentName) {
        for (EnchantmentRarity enchantmentRarity : enchantmentRarities) {
            if (enchantmentRarity.getName().equalsIgnoreCase(enchantmentName)) {
                return enchantmentRarity;
            }
        }
        return null;
    }

    public List<PickaxeEnchantment> getPickaxeEnchantments() {
        return pickaxeEnchantments;
    }

    public static class PickaxeEnchantment {

        private String name;
        private int maxLevel;
        private EnchantmentType type;
        private EnchantmentRarity rarity;

        public PickaxeEnchantment(String name, int maxLevel, EnchantmentType type, EnchantmentRarity rarity) {
            this.name = name;
            this.maxLevel = maxLevel;
            this.type = type;
            this.rarity = rarity;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public void setMaxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
        }

        public EnchantmentType getType() {
            return type;
        }

        public void setType(EnchantmentType type) {
            this.type = type;
        }

        public EnchantmentRarity getRarity() {
            return rarity;
        }

        public void setRarity(EnchantmentRarity rarity) {
            this.rarity = rarity;
        }
    }

    public static class EnchantmentType {

        private int id;
        private String name;

        public EnchantmentType(int id, String name) {
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

    public static class EnchantmentRarity {

        private int id;
        private String name;
        private String colorPrefix;
        private int order;

        public EnchantmentRarity(int id, String name, String colorPrefix, int order) {
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
}
