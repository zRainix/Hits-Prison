package de.hits.prison.prisonPlayer.scheduler;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerExpCache;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerObsidianShardsCache;
import de.hits.prison.prisonPlayer.cache.impl.TopPlayerVolcanicAshCache;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginLogger;

import java.util.logging.Logger;

@Component
@Scheduler
public class TopPlayerCacheScheduler extends CustomScheduler {

    @Autowired
    private static Logger logger;

    @Autowired
    private static TopPlayerExpCache topPlayerExpCache;
    @Autowired
    private static TopPlayerVolcanicAshCache topPlayerVolcanicAshCache;
    @Autowired
    private static TopPlayerObsidianShardsCache topPlayerObsidianShardsCache;

    private static final long fiveMinutes = 5L * 60L * 20L;
    private static final long ticksToMillis = 1000L / 20L;

    private static final long delay = 0L;
    private static final long period = fiveMinutes;

    private long nextUpdate;

    public TopPlayerCacheScheduler() {
        super(delay, period);

        long nextDelay = delay * ticksToMillis;

        this.nextUpdate = System.currentTimeMillis() + nextDelay;

        logger.info("First init all updateCachedTopPlayers");
    }

    @Override
    public void run() {
        long nextPeriod = period * ticksToMillis;

        this.nextUpdate = System.currentTimeMillis() + nextPeriod;

        topPlayerExpCache.updateTopPlayers();
        topPlayerVolcanicAshCache.updateTopPlayers();
        topPlayerObsidianShardsCache.updateTopPlayers();

        logger.info("UPDATED updateCachedTopPlayers");
    }

    public String getTimeUntilNextUpdate() {
        long currentTime = System.currentTimeMillis();

        long difference = this.nextUpdate - currentTime;

        long minutes = (difference / 1000L) / 60L;
        long seconds = (difference / 1000L) % 60L;

        StringBuilder timeBuilder = new StringBuilder();
        if (minutes != 0) {
            if (minutes < 10) {
                timeBuilder.append(0);
            }
            timeBuilder.append(minutes).append(":");
        }

        if (seconds < 10) {
            timeBuilder.append(0);
        }
        timeBuilder.append(seconds);

        return timeBuilder.toString();
    }

}
