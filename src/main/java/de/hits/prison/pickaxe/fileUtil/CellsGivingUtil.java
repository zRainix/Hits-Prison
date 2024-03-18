package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SettingsFile
public class CellsGivingUtil extends FileUtil {

    private final Logger logger = Bukkit.getLogger();
    private List<CellsGivingLevel> cellsGivingLevelsList;

    public CellsGivingUtil() {
        super("CellsGiving.yml");
        this.cellsGivingLevelsList = new ArrayList<>();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Level", null);

        for (CellsGivingLevel cellsGivingLevel : cellsGivingLevelsList) {

            for(CellsGivingDrop drop : cellsGivingLevel.getDrops()) {
                String path = "Level." + cellsGivingLevel.getLevel() + "." + drop.getItemType();
                cfg.set(path + ".ItemType", drop.getItemType());
                cfg.set(path + ".Chance", drop.getChance().toString());
                cfg.set(path + ".Amount", drop.getAmount());
            }
        }

        saveConfig();
        logger.log(Level.INFO, "CellsGiving values saved.");
    }

    @Override
    public void load() {
        loadConfig();

        ConfigurationSection levelSection = cfg.getConfigurationSection("Level");
        if(levelSection == null) return;
        this.cellsGivingLevelsList.clear();

        for(String levelString : levelSection.getKeys(false)) {
            int level = Integer.parseInt(levelString);
            ConfigurationSection dropSection = levelSection.getConfigurationSection(levelString);
            if(dropSection == null) continue;

            List<CellsGivingDrop> drops = new ArrayList<>();
            for(String itemType : dropSection.getKeys(false)) {
                    String displayName = dropSection.getString(itemType + ".ItemName", "key");
                    BigDecimal chance = new BigDecimal(dropSection.getString(itemType + ".Chance", "0"));
                    int amount = dropSection.getInt(itemType + ".Amount", 0);

                    drops.add(new CellsGivingDrop(itemType, chance, amount));
            }
            this.cellsGivingLevelsList.add(new CellsGivingLevel(level, drops));
        }
    }

    public List<CellsGivingLevel> getCellsGivingLevelsList() {
        return cellsGivingLevelsList;
    }

    public CellsGivingUtil.CellsGivingLevel getCellsGivingLevel(int level) {
        for(CellsGivingUtil.CellsGivingLevel cellsGivingLevel : getCellsGivingLevelsList()) {
            if(cellsGivingLevel.level == level) {
                return cellsGivingLevel;
            }
        }
        return null;
    }

    public static class CellsGivingLevel {

        int level;
        List<CellsGivingDrop> drops;

        public CellsGivingLevel(int level, List<CellsGivingDrop> drops) {
            this.level = level;
            this.drops = drops;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public List<CellsGivingDrop> getDrops() {
            return drops;
        }

        public void setDrops(List<CellsGivingDrop> drops) {
            this.drops = drops;
        }
    }

    public static class CellsGivingDrop {

        String itemType;
        BigDecimal chance;
        int amount;

        public CellsGivingDrop(String itemType, BigDecimal chance, int amount) {
            this.itemType = itemType;
            this.chance = chance;
            this.amount = amount;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        public BigDecimal getChance() {
            return chance;
        }

        public void setChance(BigDecimal chance) {
            this.chance = chance;
        }
    }
}
