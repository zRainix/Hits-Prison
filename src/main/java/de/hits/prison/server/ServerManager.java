package de.hits.prison.server;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.server.command.FileUtilCommand;
import de.hits.prison.server.placeholder.PlaceholderHelper;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

@Component
public class ServerManager implements Manager {

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        AutowiredManager.register(new PlaceholderHelper());

        // Commands
        FileUtilCommand fileUtilCommand = new FileUtilCommand();
        hitsPrison.registerCommand(fileUtilCommand);

        // Listener

    }
}
