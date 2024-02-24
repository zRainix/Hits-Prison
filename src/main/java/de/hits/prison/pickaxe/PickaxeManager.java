package de.hits.prison.pickaxe;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.pickaxe.command.EnchantmentCommand;
import de.hits.prison.pickaxe.enchantment.listener.BlockBreakListener;
import de.hits.prison.pickaxe.enchantment.listener.RightClickAirListener;
import de.hits.prison.pickaxe.enchantment.listener.RightClickBlockListener;
import de.hits.prison.pickaxe.enchantment.listener.RightClickEntityListener;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.listener.PickaxeFlagsListener;
import org.bukkit.plugin.PluginManager;

@Component
public class PickaxeManager implements Manager {

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        // Commands
        EnchantmentCommand enchantmentCommand = new EnchantmentCommand();
        hitsPrison.registerCommand("enchantment", enchantmentCommand);

        // Listener
        pluginManager.registerEvents(new BlockBreakListener(), hitsPrison);
        pluginManager.registerEvents(new PickaxeFlagsListener(), hitsPrison);
        pluginManager.registerEvents(new RightClickAirListener(), hitsPrison);
        pluginManager.registerEvents(new RightClickBlockListener(), hitsPrison);
        pluginManager.registerEvents(new RightClickEntityListener(), hitsPrison);

        AutowiredManager.register(new PickaxeHelper());
    }
}
