package de.hits.prison.pickaxe.anno;

import de.hits.prison.pickaxe.helper.enums.EnchantmentRarity;
import de.hits.prison.pickaxe.helper.enums.EnchantmentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnchantmentMethod {
    String enchantment();
    EnchantmentType enchantmentType();
    EnchantmentRarity enchantmentRarity();

}
