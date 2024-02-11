package de.hits.prison.mechanic.pickaxe.fileUtil;

import de.hits.prison.fileUtil.anno.SettingsFile;
import de.hits.prison.fileUtil.helper.FileUtil;
import de.hits.prison.mechanic.pickaxe.helper.EnchantmentRarity;
import de.hits.prison.mechanic.pickaxe.helper.EnchantmentType;
import de.hits.prison.mechanic.pickaxe.helper.PickaxeEnchantment;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@SettingsFile
public class PickaxeUtil extends FileUtil {

    List<PickaxeEnchantment> pickaxeEnchantments;
    List<EnchantmentType> enchantmentTypes;

    public PickaxeUtil() {
        super("pickaxe.yml");
        pickaxeEnchantments = new ArrayList<>();
        enchantmentTypes = new ArrayList<>();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Enchantment", null);
        for (PickaxeEnchantment enchantment : pickaxeEnchantments) {
            cfg.set("Enchantment." + enchantment.getName() + ".MaxLevel", enchantment.getMaxLevel());
        }
        cfg.set("EnchantmentType", null);
        for (EnchantmentType enchantmentType : enchantmentTypes) {
            cfg.set("EnchantmentType." + enchantmentType.getName() + ".Name", enchantmentType.getName());
        }
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();
        if (!cfg.contains("Enchantment")) {
            return;
        }
        ConfigurationSection enchantmentSection = cfg.getConfigurationSection("Enchantment");
        for (String name : enchantmentSection.getKeys(false)) {

            int maxLevel = enchantmentSection.getInt(name + ".MaxLevel");
            int enchantmentType = enchantmentSection.getInt(name + ".EnchantmentType");

            pickaxeEnchantments.add(new PickaxeEnchantment(name, maxLevel, enchantmentType));
        }

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

    public PickaxeEnchantment getPickaxeEnchantment(String name) {
        for(PickaxeEnchantment enchantment : pickaxeEnchantments) {
            if(enchantment.getName().equalsIgnoreCase(name)) {
                return enchantment;
            }
        }
        return null;
    }

    private int getEnchantmentTypeId(String enchantmentName) {
        for (EnchantmentType enchantmentType : enchantmentTypes) {
            if (enchantmentType.getName().equalsIgnoreCase(enchantmentName)) {
                return enchantmentType.getId();
            }
        }
        return -1;
    }

    public List<PickaxeEnchantment> getPickaxeEnchantments() {
        return pickaxeEnchantments;
    }
}
