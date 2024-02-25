package de.hits.prison.pickaxe.blocks;

import org.bukkit.Material;

public class BlockValue {

        private Material material;
        private BlockRarities rarity;
        private int volcanicAsh;
        private int obsidianShards;
        private int exp;

        public BlockValue(Material material, BlockRarities rarity, int volcanicAsh, int obsidianShards, int exp) {
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
