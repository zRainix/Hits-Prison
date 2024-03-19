package de.hits.prison.pickaxe.fileUtil;


import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import de.hits.prison.pickaxe.blocks.BlockRarities;
import de.hits.prison.pickaxe.blocks.BlockValue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SettingsFile
public class KeyFinderUtil extends FileUtil {
    private final Logger logger = Bukkit.getLogger();

    private List<KeyFinderLevel> keyFinderLevelList;

    public KeyFinderUtil() {
        super("KeyFinder.yml");
        this.keyFinderLevelList = new ArrayList<>();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Level", null);

        for (KeyFinderLevel keyFinderLevel : keyFinderLevelList) {

            for(KeyFinderLevelDrop drop : keyFinderLevel.getDrops()) {
                String path = "Level." + keyFinderLevel.getLevel() + "." + drop.getItemName();
                cfg.set(path + ".DisplayName", drop.getDisplayName());
                cfg.set(path + ".Chance", drop.getChance().toString());
                cfg.set(path + ".Rarity", drop.getRarity());
            }
        }

        saveConfig();
        logger.log(Level.INFO, "KeyFinder values saved.");
    }

    @Override
    public void load() {
        loadConfig();

        ConfigurationSection levelSection = cfg.getConfigurationSection("Level");
        if(levelSection == null) return;
        this.keyFinderLevelList.clear();

        for(String levelString : levelSection.getKeys(false)) {
            int level = Integer.parseInt(levelString);
            ConfigurationSection dropSection = levelSection.getConfigurationSection(levelString);
            if(dropSection == null) continue;

            List<KeyFinderLevelDrop> drops = new ArrayList<>();
            for(String itemName : dropSection.getKeys(false)) {
                String displayName = dropSection.getString(itemName + ".DisplayName", "key");
                BigDecimal chance = new BigDecimal(dropSection.getString(itemName + ".Chance", "0"));
                KeyFinderRaritys rarity = KeyFinderRaritys.valueOf(dropSection.getString(itemName + "Rarity", "rarity"));
                drops.add(new KeyFinderLevelDrop(displayName, itemName, chance, rarity));
            }
            this.keyFinderLevelList.add(new KeyFinderLevel(level, drops));
        }

    }

    public List<KeyFinderLevel> getKeyFinderLevelList() {
        return keyFinderLevelList;
    }

    public KeyFinderLevel getKeyFinderLevel(int level) {
        for(KeyFinderLevel keyFinderLevel : keyFinderLevelList) {
            if(keyFinderLevel.level == level) {
                return keyFinderLevel;
            }
        }
        return null;
    }

    public static class KeyFinderLevel {

        int level;
        List<KeyFinderLevelDrop> drops;

        public KeyFinderLevel(int level, List<KeyFinderLevelDrop> drops) {
            this.level = level;
            this.drops = drops;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public List<KeyFinderLevelDrop> getDrops() {
            return drops;
        }

        public void setDrops(List<KeyFinderLevelDrop> drops) {
            this.drops = drops;
        }
    }

    public static class KeyFinderLevelDrop {

        private String displayName;
        private String itemName;
        private BigDecimal chance;
        private KeyFinderRaritys rarity;


        public KeyFinderLevelDrop(String displayName, String itemName, BigDecimal chance, KeyFinderRaritys rarity) {
            this.displayName = displayName;
            this.itemName = itemName;
            this.chance = chance;
            this.rarity = rarity;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getItemName() {
            return this.itemName;
        }

        public KeyFinderRaritys getRarity() {
            return KeyFinderRaritys.valueOf(String.valueOf(this.rarity));
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public BigDecimal getChance() {
            return this.chance;
        }

        public void setChance(BigDecimal chance) {
            this.chance = chance;
        }
    }

    public enum KeyFinderRaritys {

        RARE,
        LEGENDARY,
        MYTHIC,
        TOKEN,
        RANK,
        MONTHLY,
        FLORAL;

    }

}
