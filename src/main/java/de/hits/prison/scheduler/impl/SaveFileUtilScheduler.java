package de.hits.prison.scheduler.impl;

import de.hits.prison.scheduler.CustomScheduler;
import de.hits.prison.util.FileUtilManager;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public class SaveFileUtilScheduler extends CustomScheduler {

    private Logger logger = Bukkit.getLogger();

    private final FileUtilManager fileUtilManager;

    private static final long fiveMinutes = 5L * 60L * 20L;

    public SaveFileUtilScheduler(FileUtilManager fileUtilManager) {
        super(fiveMinutes, fiveMinutes);

        this.fileUtilManager = fileUtilManager;

        logger.info("Scheduler " + getClass().getSimpleName() + " started.");
    }

    @Override
    public void run() {
        this.fileUtilManager.saveAll();
        logger.info("Saved all file utils.");
    }
}
