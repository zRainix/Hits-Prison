package de.hits.prison.mechanic.server;

import de.hits.prison.HitsPrison;
import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.fileUtil.helper.FileUtilManager;
import de.hits.prison.mechanic.helper.BaseManager;
import de.hits.prison.mechanic.server.command.FileUtilCommand;
import org.bukkit.plugin.PluginManager;

@Component
public class ServerManager implements BaseManager {


    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {

        // Commands
        FileUtilCommand fileUtilCommand = new FileUtilCommand();
        hitsPrison.getCommand("fileUtil").setExecutor(fileUtilCommand);

        // Listener

    }

}
