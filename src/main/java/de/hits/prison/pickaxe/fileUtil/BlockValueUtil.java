package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import de.hits.prison.pickaxe.blocks.BlockValue;
import de.hits.prison.pickaxe.blocks.BlockRarities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SettingsFile
public class BlockValueUtil extends FileUtil {

    private final Logger logger = Bukkit.getLogger();

    private List<BlockValue> blockValues;

    public BlockValueUtil() {
        super("BlockValue.yml");
        this.blockValues = new ArrayList<>();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        cfg.set("Blocks", null);

        for (BlockValue blockValue : blockValues) {
            Material material = blockValue.getMaterial();

            String blockTypeName = material.toString();
            String path = "Blocks." + blockTypeName;

            cfg.set(path + ".Rarity", blockValue.getRarity().name());
            cfg.set(path + ".VolcanicAsh", blockValue.getVolcanicAsh());
            cfg.set(path + ".ObsidianShards", blockValue.getObsidianShards());
            cfg.set(path + ".Exp", blockValue.getExp());
        }

        saveConfig();
        logger.log(Level.INFO, "Block values saved.");
    }

    @Override
    public void load() {
        loadConfig();
        loadBlockValues();
    }

    private void loadBlockValues() {
        ConfigurationSection blocksSection = cfg.getConfigurationSection("Blocks");

        if (blocksSection == null) {
            return;
        }
        blockValues.clear();

        for (String blockTypeName : blocksSection.getKeys(false)) {
            Material material = Material.getMaterial(blockTypeName);
            if (material != null) {
                String rarityName = blocksSection.getString(blockTypeName + ".Rarity", "COMMON");
                BlockRarities rarity = BlockRarities.valueOf(rarityName.toUpperCase());
                int volcanicAsh = blocksSection.getInt(blockTypeName + ".VolcanicAsh", 0);
                int obsidianShards = blocksSection.getInt(blockTypeName + ".ObsidianShards", 0);
                int exp = blocksSection.getInt(blockTypeName + ".Exp", 0);

                blockValues.add(new BlockValue(material, rarity, volcanicAsh, obsidianShards, exp));
            }
        }
    }

    public List<BlockValue> getBlocks() {
        return blockValues;
    }

    public BlockValue getBlockValue(Material material) {
        for(BlockValue blockValue : blockValues) {
            if(blockValue.getMaterial() == material) {
                return blockValue;
            }
        }
        return null;
    }

}
