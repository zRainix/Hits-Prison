package de.hits.prison.base.scheduler;

import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.scheduler.anno.Scheduler;
import de.hits.prison.server.scheduler.helper.CustomScheduler;
import de.hits.prison.server.fileUtil.helper.FileUtilManager;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

@Component
@Scheduler
public class SaveFileUtilScheduler extends CustomScheduler {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static FileUtilManager fileUtilManager;

    private static final long fiveMinutes = 5L * 60L * 20L;

    public SaveFileUtilScheduler() {
        super(fiveMinutes, fiveMinutes);

        logger.info("Scheduler " + getClass().getSimpleName() + " started.");
    }

    @Override
    public void run() {
        this.fileUtilManager.saveAll();
        logger.info("Saved all file utils.");
    }
}
