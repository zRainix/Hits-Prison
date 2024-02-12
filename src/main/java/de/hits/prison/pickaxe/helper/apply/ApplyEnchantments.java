package de.hits.prison.pickaxe.helper.apply;

import de.hits.prison.base.util.ItemBuilder;
import de.hits.prison.pickaxe.anno.ApplyEnchantment;
import de.hits.prison.server.model.entity.PlayerEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ApplyEnchantments {

    public void applyEnchantment(PlayerEnchantment playerEnchantment, ItemBuilder itemBuilder) {
        Method method = findMethod(playerEnchantment);
        if (method == null)
            return;
        try {
            AppliedEnchantment appliedEnchantment = (AppliedEnchantment) method.invoke(this, playerEnchantment);
            itemBuilder.addEnchant(appliedEnchantment.getEnchantment(), appliedEnchantment.getLevel(), true);
        } catch (IllegalAccessException | InvocationTargetException e) {
        }
    }

    @ApplyEnchantment("Efficiency")
    private AppliedEnchantment applyEfficiency(PlayerEnchantment playerEnchantment) {
        return new AppliedEnchantment(Enchantment.DIG_SPEED, playerEnchantment.getEnchantmentLevel());
    }

    private Method findMethod(PlayerEnchantment playerEnchantment) {
        for (Method method : getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(ApplyEnchantment.class))
                continue;
            ApplyEnchantment applyEnchantment = method.getAnnotation(ApplyEnchantment.class);
            if (!applyEnchantment.value().equals(playerEnchantment.getEnchantmentName()))
                continue;
            return method;
        }
        return null;
    }

    private class AppliedEnchantment {
        Enchantment enchantment;
        int level;

        public AppliedEnchantment(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }

        public Enchantment getEnchantment() {
            return enchantment;
        }

        public void setEnchantment(Enchantment enchantment) {
            this.enchantment = enchantment;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }

}
