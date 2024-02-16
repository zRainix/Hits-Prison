package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedEnchantment extends PickaxeEnchantmentImpl {
    public SpeedEnchantment() {
        super("Speed");
    }

    @Override
    public PotionEffect getVanillaPotionEffect(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment) {
        return new PotionEffect(PotionEffectType.SPEED, 20 * 3, playerEnchantment.getEnchantmentLevel() - 1);
    }
}
