package de.hits.prison.cell;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.cell.command.CellCommand;
import org.bukkit.plugin.PluginManager;

public class CellManager implements Manager {
    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        AutowiredManager.register(new CellManager());

        //Commands
        CellCommand cellCommand = new CellCommand();
        hitsPrison.registerCommand(cellCommand);

        //Listener
    }
}
