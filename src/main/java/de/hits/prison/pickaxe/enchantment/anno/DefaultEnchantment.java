package de.hits.prison.pickaxe.enchantment.anno;

import org.bukkit.Material;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultEnchantment {
    String description() default "";

    Material previewMaterial() default Material.DIAMOND;

    int maxLevel();

    String activationPrice();

    String priceMultiplier();

    String type();

    String rarity() default "Common";
}
