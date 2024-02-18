package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.helper.VanillaEnchantment;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.enchantments.Enchantment;

@DefaultEnchantment(maxLevel = 20, description = "Adds vanilla efficiency enchantment for faster mining.", activationPrice = "100", priceMultiplier = "1.5", type = "Vanilla Enchantment")
public class EfficiencyEnchantment extends PickaxeEnchantmentImpl {

    public EfficiencyEnchantment() {
        super("Efficiency");
    }

    @Override
    public VanillaEnchantment getVanillaEnchantment(PrisonPlayer prisonPlayer, PlayerEnchantment efficiencyEnchantment) {
        return new VanillaEnchantment(Enchantment.DIG_SPEED, efficiencyEnchantment.getEnchantmentLevel());
    }
}
