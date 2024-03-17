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
    private long delay;
    private long period;

    public CustomScheduler(long delay, long period) {
        this.delay = delay;
        this.period = period;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
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
