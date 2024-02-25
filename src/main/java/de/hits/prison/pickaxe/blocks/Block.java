package de.hits.prison.pickaxe.blocks;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Block {

    private Map<Material, BlockData> blockDataMap;

    public Block() {
        this.blockDataMap = new HashMap<>();
    }

    public void addAllMinecraftBlocks() {
        for (Material material : Material.values()) {
                if (material.isBlock()) {
                    addBlock(material, BlockRarities.COMMON, 300, 200, 100);
                }
        }
    }

    public void addBlock(Material material, BlockRarities rarity, int volcanicAsh, int obsidianShards, int exp) {
        if (!checkIfDuplicateBlock(material)) {
            BlockData blockData = new BlockData(material, rarity, volcanicAsh, obsidianShards, exp);
            this.blockDataMap.put(material, blockData);
        }
    }

    public BlockData getBlockData(Material material) {
        return blockDataMap.get(material);
    }

    public Map<Material, BlockData> getAllBlocks() {
        return blockDataMap;
    }

    private boolean checkIfDuplicateBlock(Material material) {
        return this.blockDataMap.containsKey(material);
    }

    public static class BlockData {
        private Material material;
        private BlockRarities rarity;
        private int volcanicAsh;
        private int obsidianShards;
        private int exp;

        public BlockData(Material material, BlockRarities rarity, int volcanicAsh, int obsidianShards, int exp) {
            this.material = material;
            this.rarity = rarity;
            this.volcanicAsh = volcanicAsh;
            this.obsidianShards = obsidianShards;
            this.exp = exp;
        }

        public Material getMaterial() {
            return material;
        }

        public BlockRarities getRarity() {
            return rarity;
        }

        public int getVolcanicAsh() {
            return volcanicAsh;
        }

        public int getObsidianShards() {
            return obsidianShards;
        }

        public int getExp() {
            return exp;
        }
    }
}
