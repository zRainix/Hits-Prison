package de.hits.scheduler.impl;

import de.hits.scheduler.CustomScheduler;
import de.hits.util.FileUtilManager;

public class SaveThoseSchedulers extends CustomScheduler {

    private final FileUtilManager fileUtilManager;
    public SaveThoseSchedulers(FileUtilManager fileUtilManager, long delay, long period) {
        super(delay, period);

        this.fileUtilManager = fileUtilManager;
    }

    @Override
    public void run() {
        fileUtilManager.saveAll();
    }
}
