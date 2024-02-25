package de.hits.prison.pickaxe.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import de.hits.prison.pickaxe.blocks.Block;
import de.hits.prison.pickaxe.blocks.BlockRarities;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@SettingsFile
public class BlockValueUtil extends FileUtil {

    private final Logger logger = Bukkit.getLogger();

    private Block block;

    public BlockValueUtil() {
        super("BlockValue.yml");
        this.block = new Block();
    }

    @Override
    public void init() {
        saveDefaultsConfig();
        block.addAllMinecraftBlocks();
    }

    @Override
    public void save() {
        cfg.set("Blocks", null);

        for (Map.Entry<Material, Block.BlockData> entry : block.getAllBlocks().entrySet()) {
            Material material = entry.getKey();
            Block.BlockData blockData = entry.getValue();

            String blockTypeName = material.toString();
            String path = "Blocks." + blockTypeName;

            cfg.set(path + ".Rarity", blockData.getRarity().name());
            cfg.set(path + ".VolcanicAsh", blockData.getVolcanicAsh());
            cfg.set(path + ".ObsidianShards", blockData.getObsidianShards());
            cfg.set(path + ".Exp", blockData.getExp());
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

        for (String blockTypeName : blocksSection.getKeys(false)) {
            Material material = Material.getMaterial(blockTypeName);
            if (material != null) {
                String rarityName = blocksSection.getString(blockTypeName + ".Rarity", "COMMON");
                BlockRarities rarity = BlockRarities.valueOf(rarityName.toUpperCase());
                int volcanicAsh = blocksSection.getInt(blockTypeName + ".VolcanicAsh", 0);
                int obsidianShards = blocksSection.getInt(blockTypeName + ".ObsidianShards", 0);
                int exp = blocksSection.getInt(blockTypeName + ".Exp", 0);

                block.addBlock(material, rarity, volcanicAsh, obsidianShards, exp);
            }
        }
    }

    public Block getBlock() {
        return block;
    }

}
