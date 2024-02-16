package de.hits.prison.server.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta meta;

    private int repeats = 1;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(String displayName) {
        if (meta != null) {
            meta.setDisplayName(displayName);
        }
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (meta != null) {
            meta.setLore(lore);
        }
        return this;
    }

    public ItemBuilder addLoreBreak() {
        if (meta != null) {
            List<String> currentLore = meta.getLore();
            if (currentLore == null)
                currentLore = new ArrayList<>();
            currentLore.add("§a".repeat(repeats++));
            meta.setLore(currentLore);
        }
        return this;
    }

    public ItemBuilder addLore(String lore) {
        if (meta != null) {
            List<String> currentLore = meta.getLore();
            if (currentLore == null)
                currentLore = new ArrayList<>();
            currentLore.add(lore);
            meta.setLore(currentLore);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    public ItemBuilder setDamage(int damage) {
        if (meta instanceof Damageable damageableMeta) {
            damageableMeta.setDamage(damage);
        }
        return this;
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        if (meta != null) {
            meta.addItemFlags(itemFlags);
        }
        return this;
    }

    public ItemBuilder setAllItemFlags() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.values());
        }
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
        if (meta != null) {
            meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        }
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
