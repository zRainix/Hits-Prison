package de.hits.prison.mechanic.pickaxe.fileUtil;

import de.hits.prison.fileUtil.anno.SettingsFile;
import de.hits.prison.fileUtil.helper.FileUtil;
import de.hits.prison.mechanic.pickaxe.helper.PickaxeEnchantment;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@SettingsFile
public class PickaxeUtil extends FileUtil {

    List<PickaxeEnchantment> pickaxeEnchantments;

    public PickaxeUtil() {
        super("pickaxe.yml");
        pickaxeEnchantments = new ArrayList<>();
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
            pickaxeEnchantments.add(new PickaxeEnchantment(name, maxLevel));
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

    public List<PickaxeEnchantment> getPickaxeEnchantments() {
        return pickaxeEnchantments;
    }
}
