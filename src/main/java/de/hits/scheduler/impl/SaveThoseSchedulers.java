package de.hits.scheduler.impl;

import de.hits.scheduler.CustomScheduler;
import de.hits.util.FileUtilManager;

public class SaveThoseSchedulers extends CustomScheduler {

    private final FileUtilManager fileUtilManager;
    public SaveThoseSchedulers(FileUtilManager fileUtilManager) {
        super(0, 5*20);

        this.fileUtilManager = fileUtilManager;

        System.out.println("yep yep");
    }

    @Override
    public void run() {
        this.fileUtilManager.saveAll();
    }
}
