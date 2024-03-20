package de.hits.prison.server.util;


import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkEffectMeta;

public class FireworkStarBuilder extends ItemBuilder {
    public FireworkStarBuilder() {
        super(Material.FIREWORK_STAR);
    }

    public FireworkStarBuilder setColor(Color color) {
        ((FireworkEffectMeta) meta).setEffect(FireworkEffect.builder().withColor(color).build());
        return this;
    }
}
