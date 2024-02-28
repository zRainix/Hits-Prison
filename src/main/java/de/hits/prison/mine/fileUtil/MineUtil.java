package de.hits.prison.mine.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@SettingsFile
public class MineUtil extends FileUtil {

    List<AreaLevel> areaLevels;
    List<BlockLevel> blockLevels;
    private static final AreaLevel defaultAreaLevel = new AreaLevel();
    private static final BlockLevel defaultBlockLevel = new BlockLevel();


    public MineUtil() {
        super("mines.yml");
        areaLevels = new ArrayList<>();
        blockLevels = new ArrayList<>();
    }

    @Override
    public void init() {
        loadConfig();
        if (!cfg.contains("AreaLevel")) {
            cfg.addDefault("AreaLevel." + defaultAreaLevel.getLevel() + ".MineSize", defaultAreaLevel.getMineSize());
            cfg.addDefault("AreaLevel." + defaultAreaLevel.getLevel() + ".MineDepth", defaultAreaLevel.getMineDepth());
        }
        if (!cfg.contains("BlockLevel")) {
            for (Material material : defaultBlockLevel.blocks.keySet()) {
                cfg.set("BlockLevel." + defaultBlockLevel.getLevel() + "." + material.name(), defaultBlockLevel.blocks.get(material));
            }
        }
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("AreaLevel", null);
        for (AreaLevel areaLevel : areaLevels) {
            cfg.set("AreaLevel." + areaLevel.getLevel() + ".MineSize", areaLevel.getMineSize());
            cfg.set("AreaLevel." + areaLevel.getLevel() + ".MineDepth", areaLevel.getMineDepth());
        }
        cfg.set("BlockLevel", null);
        for (BlockLevel blockLevel : blockLevels) {
            for (Map.Entry<Material, Integer> entry : blockLevel.getBlocks().entrySet()) {
                cfg.set("BlockLevel." + blockLevel.getLevel() + "." + entry.getKey().name(), entry.getValue());
            }
        }
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();

        loadAreaLevels();
        loadBlockLevels();
    }

    private void loadAreaLevels() {
        ConfigurationSection areaLevelSection = cfg.getConfigurationSection("AreaLevel");
        if (areaLevelSection == null)
            return;
        areaLevels.clear();
        for (String levelString : areaLevelSection.getKeys(false)) {
            int level = Integer.parseInt(levelString);
            int mineSize = areaLevelSection.getInt(levelString + ".MineSize", defaultAreaLevel.getMineSize());
            int mineDepth = areaLevelSection.getInt(levelString + ".MineDepth", defaultAreaLevel.getMineDepth());
            areaLevels.add(new AreaLevel(level, mineSize, mineDepth));
        }
    }

    private void loadBlockLevels() {
        ConfigurationSection blockLevelSection = cfg.getConfigurationSection("BlockLevel");
        if (blockLevelSection == null)
            return;
        blockLevels.clear();
        for (String levelString : blockLevelSection.getKeys(false)) {
            int level = Integer.parseInt(levelString);
            Map<Material, Integer> map = new HashMap<>();
            ConfigurationSection levelSection = blockLevelSection.getConfigurationSection(levelString);
            for (String materialString : levelSection.getKeys(false)) {
                Material material = Material.getMaterial(materialString.toUpperCase());
                int amount = levelSection.getInt(materialString, 100);
                map.put(material, amount);
            }
            blockLevels.add(new BlockLevel(level, map));
        }
    }

    public AreaLevel getAreaLevel(int level) {
        for (AreaLevel areaLevel : areaLevels) {
            if (areaLevel.getLevel() == level)
                return areaLevel;
        }
        return null;
    }

    public BlockLevel getBlockLevel(int level) {
        for (BlockLevel blockLevel : blockLevels) {
            if (blockLevel.getLevel() == level)
                return blockLevel;
        }
        return null;
    }

    private final HashMap<Integer, BlockLevel> blockLevelCache = new HashMap<>();

    public HashMap<Integer, BlockLevel> getBlockLevelCache() {
        return blockLevelCache;
    }

    public BlockLevel calculateBlockLevel(int level) {
        BlockLevel blockLevel = getBlockLevel(level);
        if (blockLevel != null)
            return blockLevel;
        if (blockLevelCache.containsKey(level))
            return blockLevelCache.get(level);

        Optional<BlockLevel> nextHighest = blockLevels.stream().filter(bl -> bl.getLevel() > level).min(Comparator.comparingInt(BlockLevel::getLevel));
        Optional<BlockLevel> nextLowest = blockLevels.stream().filter(bl -> bl.getLevel() < level).max(Comparator.comparingInt(BlockLevel::getLevel));
        if (nextLowest.isEmpty())
            return null;
        BlockLevel lowest = nextLowest.get();
        if (nextHighest.isEmpty())
            return lowest;
        BlockLevel highest = nextHighest.get();

        int levelLow = lowest.getLevel();
        int levelHigh = highest.getLevel();

        int diff = levelHigh - levelLow;

        int relative = level - levelLow;

        BigDecimal partOfHighest = BigDecimal.valueOf(relative).divide(BigDecimal.valueOf(diff), 3, RoundingMode.HALF_UP);
        BigDecimal partOfLowest = BigDecimal.ONE.subtract(partOfHighest);
        Map<Material, Integer> blocks = new HashMap<>();

        int sumLower = lowest.getBlocks().values().stream().reduce(Integer::sum).orElse(0);
        int sumHigher = highest.getBlocks().values().stream().reduce(Integer::sum).orElse(0);

        for (Map.Entry<Material, Integer> lowerBlock : lowest.getBlocks().entrySet()) {
            Material material = lowerBlock.getKey();
            int amount = lowerBlock.getValue();

            BigDecimal decimal = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(sumLower), 3, RoundingMode.HALF_UP).multiply(partOfLowest).multiply(BigDecimal.valueOf(10000));

            blocks.put(material, decimal.intValue());
        }

        for (Map.Entry<Material, Integer> higherBlock : highest.getBlocks().entrySet()) {
            Material material = higherBlock.getKey();
            int amount = higherBlock.getValue();

            BigDecimal decimal = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(sumHigher), 3, RoundingMode.HALF_UP).multiply(partOfHighest).multiply(BigDecimal.valueOf(10000));

            amount = decimal.intValue();

            if (blocks.containsKey(material)) {
                amount += blocks.remove(material);
            }
            blocks.put(material, amount);
        }

        blockLevel = new BlockLevel(level, blocks);
        blockLevelCache.put(level, blockLevel);
        return blockLevel;
    }

    private final HashMap<Integer, AreaLevel> areaLevelCache = new HashMap<>();

    public HashMap<Integer, AreaLevel> getAreaLevelCache() {
        return areaLevelCache;
    }

    public AreaLevel calculateAreaLevel(int level) {
        AreaLevel areaLevel = getAreaLevel(level);
        if (areaLevel != null)
            return areaLevel;
        if (areaLevelCache.containsKey(level))
            return areaLevelCache.get(level);

        Optional<AreaLevel> nextHighest = areaLevels.stream().filter(bl -> bl.getLevel() > level).min(Comparator.comparingInt(AreaLevel::getLevel));
        Optional<AreaLevel> nextLowest = areaLevels.stream().filter(bl -> bl.getLevel() < level).max(Comparator.comparingInt(AreaLevel::getLevel));
        if (nextLowest.isEmpty())
            return null;
        AreaLevel lowest = nextLowest.get();
        if (nextHighest.isEmpty())
            return lowest;
        AreaLevel highest = nextHighest.get();

        int levelLow = lowest.getLevel();
        int levelHigh = highest.getLevel();

        int diff = levelHigh - levelLow;

        int relative = level - levelLow;

        BigDecimal partOfDiff = BigDecimal.valueOf(relative).divide(BigDecimal.valueOf(diff), 3, RoundingMode.HALF_UP);

        int sizeDiff = highest.getMineSize() - lowest.getMineSize();
        int mineSize = lowest.getMineSize() + partOfDiff.multiply(BigDecimal.valueOf(sizeDiff)).intValue();
        mineSize = Math.max(mineSize, lowest.getMineSize());
        mineSize = Math.min(mineSize, highest.getMineSize());

        int depthDiff = highest.getMineSize() - lowest.getMineSize();
        int mineDepth = lowest.getMineDepth() + partOfDiff.multiply(BigDecimal.valueOf(depthDiff)).intValue();
        mineDepth = Math.max(mineDepth, lowest.getMineDepth());
        mineDepth = Math.min(mineDepth, highest.getMineDepth());

        areaLevel = new AreaLevel(level, mineSize, mineDepth);
        areaLevelCache.put(level, areaLevel);
        return areaLevel;
    }

    public static class BlockLevel {

        int level;
        Map<Material, Integer> blocks;

        public BlockLevel() {
            this(1, Map.of(Material.STONE, 90, Material.COAL_ORE, 10));
        }

        public BlockLevel(int level, Map<Material, Integer> blocks) {
            this.level = level;
            this.blocks = blocks;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public Map<Material, Integer> getBlocks() {
            return blocks;
        }

        public void setBlocks(Map<Material, Integer> blocks) {
            this.blocks = blocks;
        }
    }

    public static class AreaLevel {

        int level;
        int mineSize;
        int mineDepth;

        public AreaLevel() {
            this(1, 40, 30);
        }

        public AreaLevel(int level, int mineSize, int mineDepth) {
            this.level = level;
            this.mineSize = mineSize;
            this.mineDepth = mineDepth;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public int getMineSize() {
            return mineSize;
        }

        public void setMineSize(int mineSize) {
            this.mineSize = mineSize;
        }

        public int getMineDepth() {
            return mineDepth;
        }

        public void setMineDepth(int mineDepth) {
            this.mineDepth = mineDepth;
        }
    }

    public static AreaLevel getDefaultAreaLevel() {
        return defaultAreaLevel;
    }

    public static BlockLevel getDefaultBlockLevel() {
        return defaultBlockLevel;
    }
}
