package de.hits.prison.server.scheduler.helper;

import de.hits.prison.server.autowire.helper.AutowiredManager;
import de.hits.prison.server.model.helper.ClassScanner;
import de.hits.prison.server.scheduler.anno.Scheduler;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class SchedulerManager {

    private Logger logger = Bukkit.getLogger();

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

    public void registerAllSchedulers(String packageName) {
        Set<Class<?>> schedulers = ClassScanner.getClassesByAnnotation(packageName, Scheduler.class);
        try {
            for (Class<?> scheduler : schedulers) {
                Scheduler schedulerAnno = scheduler.getAnnotation(Scheduler.class);

                if (scheduler.getSuperclass() == CustomScheduler.class) {
                    CustomScheduler customScheduler = (CustomScheduler) scheduler.getConstructor().newInstance();
                    registerScheduler(customScheduler);
                    if (schedulerAnno.autoStart()) {
                        customScheduler.start();
                    }
                    AutowiredManager.register(customScheduler);
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe("Error while initializing schedulers: " + e.getMessage());
        }
    }
}
