package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@DefaultEnchantment(maxLevel = 10, description = "Adds haste potion effect to player.", activationPrice = "100", priceMultiplier = "1.5", type = "Effect")
public class HasteEnchantment extends PickaxeEnchantmentImpl {
    public HasteEnchantment() {
        super("Haste");
    }

    @Override
    public PotionEffect getVanillaPotionEffect(PrisonPlayer prisonPlayer, PlayerEnchantment hasteEnchantment) {
        return new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 3, hasteEnchantment.getEnchantmentLevel() - 1);
    }
}
