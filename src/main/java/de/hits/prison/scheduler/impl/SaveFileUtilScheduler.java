package de.hits.prison.scheduler.impl;

import de.hits.prison.scheduler.CustomScheduler;
import de.hits.prison.util.FileUtilManager;

public class SaveFileUtilScheduler extends CustomScheduler {

    private final FileUtilManager fileUtilManager;
    public SaveFileUtilScheduler(FileUtilManager fileUtilManager) {
        super(0, 5*20L);

        this.fileUtilManager = fileUtilManager;

        System.out.println("yep yep");
    }

    @Override
    public void run() {
        this.fileUtilManager.saveAll();
    }
}
