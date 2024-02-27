package de.hits.prison.pickaxe;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.pickaxe.command.EnchantmentCommand;
import de.hits.prison.mine.listener.BlockBreakListener;
import de.hits.prison.mine.listener.RightClickAirListener;
import de.hits.prison.mine.listener.RightClickBlockListener;
import de.hits.prison.mine.listener.RightClickEntityListener;
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
        pluginManager.registerEvents(new PickaxeFlagsListener(), hitsPrison);

        AutowiredManager.register(new PickaxeHelper());
    }
}
