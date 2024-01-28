package de.hits.scheduler;

import java.util.ArrayList;
import java.util.List;

public class SchedulerManager {

    private List<CustomScheduler> registeredSchedulers;

    public SchedulerManager() {
        this.registeredSchedulers = new ArrayList<>();
    }

    public void registerScheduler(CustomScheduler scheduler) {
        if (!registeredSchedulers.contains(scheduler)) {
            registeredSchedulers.add(scheduler);
        }
    }

    public void unregisterScheduler(CustomScheduler scheduler) {
        if (registeredSchedulers.contains(scheduler)) {
            scheduler.stop();
            registeredSchedulers.remove(scheduler);
        }
    }

    public List<CustomScheduler> getRegisteredSchedulers() {
        return new ArrayList<>(registeredSchedulers);
    }

    public List<CustomScheduler> getRunningSchedulers() {
        List<CustomScheduler> runningSchedulers = new ArrayList<>();
        for (CustomScheduler scheduler : registeredSchedulers) {
            if (scheduler.isRunning()) {
                runningSchedulers.add(scheduler);
            }
        }
        return runningSchedulers;
    }
}
