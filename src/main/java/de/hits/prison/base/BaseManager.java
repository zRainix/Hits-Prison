package de.hits.prison.base;

import de.hits.prison.HitsPrison;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.base.command.FileUtilCommand;
import de.hits.prison.server.helper.Manager;
import org.bukkit.plugin.PluginManager;

@Component
public class BaseManager implements Manager {


    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {

        // Commands
        FileUtilCommand fileUtilCommand = new FileUtilCommand();
        hitsPrison.getCommand("fileUtil").setExecutor(fileUtilCommand);

        // Listener

    }

}
