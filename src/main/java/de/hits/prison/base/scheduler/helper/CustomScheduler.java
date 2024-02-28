package de.hits.prison.base.scheduler.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import org.bukkit.Bukkit;

@Component
public abstract class CustomScheduler implements Runnable {

    @Autowired
    private static HitsPrison main;

    private int schedulerId = -1;
    private final long delay;
    private final long period;

    public CustomScheduler(long delay, long period) {
        this.delay = delay;
        this.period = period;
    }

    public boolean isRunning() {
        return schedulerId != -1;
    }

    public void start() {
        if (!isRunning())
            schedulerId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, this, delay, period);
    }

    public void startAsync() {
        if (!isRunning())
            schedulerId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(main, this, delay, period);
    }

    public void stop() {
        if (isRunning()) {
            Bukkit.getScheduler().cancelTask(schedulerId);
            schedulerId = -1;
        }
    }
}
