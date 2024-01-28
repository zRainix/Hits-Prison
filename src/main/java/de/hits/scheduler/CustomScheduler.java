package de.hits.scheduler;

import de.hits.customhoe.CustomHoe;
import org.bukkit.Bukkit;

public abstract class CustomScheduler implements Runnable {

    private int schedulerId = -1;
    private long delay, period;

    public CustomScheduler(long delay, long period) {
        this.delay = delay;
        this.period = period;
    }

    public boolean isRunning() {
        return schedulerId != -1;
    }

    public void start() {
        if (!isRunning()) {
            schedulerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomHoe.getMain(), this, delay, period);
        }
    }

    public void stop() {
        if (isRunning()) {
            Bukkit.getScheduler().cancelTask(schedulerId);
            schedulerId = -1;
        }
    }
}
