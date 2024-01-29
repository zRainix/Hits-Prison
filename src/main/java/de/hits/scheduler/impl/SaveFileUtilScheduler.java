package de.hits.scheduler.impl;

import de.hits.scheduler.CustomScheduler;
import de.hits.util.FileUtilManager;

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
