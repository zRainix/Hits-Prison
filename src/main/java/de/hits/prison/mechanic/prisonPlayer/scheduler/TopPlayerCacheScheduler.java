package de.hits.prison.mechanic.prisonPlayer.scheduler;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerExpCache;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerObsidianShardsCache;
import de.hits.prison.mechanic.prisonPlayer.cache.impl.TopPlayerVulcanicAshCache;
import de.hits.prison.scheduler.anno.Scheduler;
import de.hits.prison.scheduler.helper.CustomScheduler;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Component
@Scheduler
public class TopPlayerCacheScheduler extends CustomScheduler {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static TopPlayerExpCache topPlayerExpCache;
    @Autowired
    private static TopPlayerVulcanicAshCache topPlayerVulcanicAshCache;
    @Autowired
    private static TopPlayerObsidianShardsCache topPlayerObsidianShardsCache;

    private static final long fiveMinutes = 5L * 60L * 20L;
    private static final long ticksToMillis = 1000L / 20L;

    private static final long delay = 0L;
    private static final long period = fiveMinutes;

    private long nextUpdate;

    public TopPlayerCacheScheduler() {
        super(delay, period);

        this.nextUpdate = System.currentTimeMillis() + (delay * ticksToMillis);

        logger.info("First init all updateCachedTopPlayers");
    }

    @Override
    public void run() {
        topPlayerExpCache.updateTopPlayers();
        topPlayerVulcanicAshCache.updateTopPlayers();
        topPlayerObsidianShardsCache.updateTopPlayers();

        logger.info("UPDATED updateCachedTopPlayers");

        this.nextUpdate = System.currentTimeMillis() + (period * ticksToMillis);
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
