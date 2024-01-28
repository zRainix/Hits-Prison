package de.hits.scheduler.impl;

import de.hits.scheduler.CustomScheduler;
import de.hits.util.FileUtilManager;

public class SaveThoseSchedulers extends CustomScheduler {

    private final FileUtilManager fileUtilManager;
    public SaveThoseSchedulers(FileUtilManager fileUtilManager) {
        super(0, 5*60*20);

        this.fileUtilManager = fileUtilManager;
    }

    @Override
    public void run() {
        fileUtilManager.saveAll();
    }
}
