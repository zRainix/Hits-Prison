package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SettingsFile
public class CellsGivingUtil extends FileUtil {

    private final Logger logger = Bukkit.getLogger();
    private List<CellsGivingLevel> cellsGivingLevelsList;
    private List<CellsGivingDrop> cellsGivingDrops;

    public CellsGivingUtil() {
        super("CellsGiving.yml");
        this.cellsGivingLevelsList = new ArrayList<>();
        this.cellsGivingDrops = new ArrayList<>();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Level", null);

        for (CellsGivingLevel cellsGivingLevel : cellsGivingLevelsList) {
            for(CellsGivingDropChance drop : cellsGivingLevel.getDrops()) {
                cfg.set("Level." + cellsGivingLevel.getLevel() + "." + drop.getDrop().getName(), drop.getChance().toString());
            }
        }

        for(CellsGivingDrop cellsGivingDrop : cellsGivingDrops) {
            cfg.set("Drop." + cellsGivingDrop.getItemType() + ".Name", cellsGivingDrop.getName());
            cfg.set("Drop." + cellsGivingDrop.getItemType() + ".ColorPrefix", cellsGivingDrop.getColorPrefix());
            for(Map.Entry<Integer, Integer> entry : cellsGivingDrop.getAmountWeights().entrySet()) {
                cfg.set("Drop." + cellsGivingDrop.getItemType() + ".Amounts." + entry.getKey(), entry.getValue());
            }
        }


        saveConfig();
        logger.log(Level.INFO, "CellsGiving values saved.");
    }

    @Override
    public void load() {
        loadConfig();

        loadDrops();
        loadLevels();
    }

    public void loadDrops() {
        ConfigurationSection dropSection = cfg.getConfigurationSection("Drop");
        if(dropSection == null) {
            return;
        }
        cellsGivingDrops.clear();

        for(String name : dropSection.getKeys(false)) {
            String itemName = dropSection.getString(name + ".ItemName", "key");
            ConfigurationSection amountsSection = dropSection.getConfigurationSection(name + ".Amounts");
            if(amountsSection == null) {
                logger.warning("Could not load Drop " + itemName);
                continue;
            }
            Map<Integer, Integer> amountsWeights = amountsSection.getKeys(false).stream().map(amountString -> {
                int amount = Integer.parseInt((amountString));
                int weight = amountsSection.getInt(amountString, 0);
                return Map.entry(amount, weight);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            String colorPrefix = dropSection.getString(name + ".ColorPrefix", "§7");


            cellsGivingDrops.add(new CellsGivingDrop(name, itemName, amountsWeights, colorPrefix));
        }
    }

    public void loadLevels() {
        ConfigurationSection levelSection = cfg.getConfigurationSection("Level");
        if(levelSection == null) return;
        this.cellsGivingLevelsList.clear();

        for(String levelString : levelSection.getKeys(false)) {
            int level = Integer.parseInt(levelString);
            ConfigurationSection dropSection = levelSection.getConfigurationSection(levelString);
            if(dropSection == null) continue;

            List<CellsGivingDropChance> dropChances = new ArrayList<>();
            for(String dropName : dropSection.getKeys(false)) {
                System.out.println(dropSection.getString(dropName));
                BigDecimal chance = new BigDecimal(dropSection.getString(dropName, "0.0"));
                CellsGivingDrop cellsGivingDrop = getCellsGivingDrop(dropName);
                if(cellsGivingDrop == null) {
                    continue;
                }

                dropChances.add(new CellsGivingDropChance(cellsGivingDrop, chance));
            }
            this.cellsGivingLevelsList.add(new CellsGivingLevel(level, dropChances));
        }
    }

    public CellsGivingDrop getCellsGivingDrop(String name) {
        for (CellsGivingDrop cellsGivingDrop : cellsGivingDrops) {
            if(cellsGivingDrop.getName().equals(name)) {
                return cellsGivingDrop;
            }
        }
        return null;
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
        List<CellsGivingDropChance> drops;

        public CellsGivingLevel(int level, List<CellsGivingDropChance> drops) {
            this.level = level;
            this.drops = drops;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public List<CellsGivingDropChance> getDrops() {
            return drops;
        }

        public void setDrops(List<CellsGivingDropChance> drops) {
            this.drops = drops;
        }
    }

    public static class CellsGivingDrop {

        String name;
        String itemType;
        String colorPrefix;
        Map<Integer, Integer> amountWeights;

        public CellsGivingDrop(String name, String itemType, Map<Integer, Integer> amountWeights, String colorPrefix) {
            this.name = name;
            this.itemType = itemType;
            this.amountWeights = amountWeights;
            this.colorPrefix = colorPrefix;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<Integer, Integer> getAmountWeights() {
            return amountWeights;
        }

        public void setAmountWeights(Map<Integer, Integer> amountWeights) {
            this.amountWeights = amountWeights;
        }

        public String getColorPrefix() {
            return colorPrefix;
        }

        public void setColorPrefix(String colorPrefix) {
            this.colorPrefix = colorPrefix;
        }

        public String getItemType() {
            return itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }
    }

    public static class CellsGivingDropChance {

        CellsGivingDrop drop;
        BigDecimal chance;

        public CellsGivingDropChance(CellsGivingDrop drop, BigDecimal chance) {
            this.drop = drop;
            this.chance = chance;
        }

        public CellsGivingDrop getDrop() {
            return drop;
        }

        public void setDrop(CellsGivingDrop drop) {
            this.drop = drop;
        }

        public BigDecimal getChance() {
            return chance;
        }

        public void setChance(BigDecimal chance) {
            this.chance = chance;
        }
    }
}
