package de.hits.prison.mechanic.pickaxe.anno;

import de.hits.prison.mechanic.pickaxe.helper.EnchantmentRarity;
import de.hits.prison.mechanic.pickaxe.helper.EnchantmentType;
import de.hits.prison.mechanic.pickaxe.helper.EnchantmentUsage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnchantmentMethod {
    String enchantment();
    EnchantmentUsage enchantmentType();
    EnchantmentRarity enchantmentRarity();

}
