package de.hits.prison.pickaxe.enchantment.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.base.model.helper.ClassScanner;
import org.bukkit.plugin.PluginManager;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PickaxeEnchantmentImplManager implements Manager {

    private final Set<PickaxeEnchantmentImpl> enchantmentsImplementations;

    public PickaxeEnchantmentImplManager() {
        enchantmentsImplementations = ClassScanner
                .getClassesBySuperclass(ClassScanner.getPackageNameOfParallelPackage(getClass().getPackageName(), "impl"), PickaxeEnchantmentImpl.class)
                .stream().map(clazz -> {
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
    }

    public Set<PickaxeEnchantmentImpl> getEnchantmentsImplementations() {
        return enchantmentsImplementations;
    }
}
