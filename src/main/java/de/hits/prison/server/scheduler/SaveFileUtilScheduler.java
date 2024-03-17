package de.hits.prison.server.scheduler;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import de.hits.prison.base.fileUtil.helper.FileUtilManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Logger;

@Component
@Scheduler
public class SaveFileUtilScheduler extends CustomScheduler {

    @Autowired
    private static Logger logger;

    @Autowired
    private static FileUtilManager fileUtilManager;

    private static final long fiveMinutes = 5L * 60L * 20L;

    public SaveFileUtilScheduler() {
        super(fiveMinutes, fiveMinutes);

        logger.info("Scheduler " + getClass().getSimpleName() + " started.");
    }

    @Override
    public void run() {
        fileUtilManager.saveAll();
        logger.info("Saved all file utils.");
    }
}
