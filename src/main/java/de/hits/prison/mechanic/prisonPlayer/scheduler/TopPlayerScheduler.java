package de.hits.prison.mechanic.prisonPlayer.scheduler;

import de.hits.prison.HitsPrison;
import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.prisonPlayer.helper.TopPlayerExpCache;
import de.hits.prison.scheduler.anno.Scheduler;
import de.hits.prison.scheduler.helper.CustomScheduler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

@Component
@Scheduler
public class TopPlayerScheduler extends CustomScheduler {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static TopPlayerExpCache topPlayerExpCache;

    @Autowired
    private static HitsPrison main;

    private static final long fiveMinutes = 5L * 60L * 20L;
    private static final long fiveSeconds = 5L * 20L;


    public TopPlayerScheduler() {
        super(0L, fiveSeconds);
        logger.info("First init all updateCachedTopPlayers");

        new BukkitRunnable() {
            @Override
            public void run() {
                String remainingTime = getTimeUntilNextUpdate();
                Bukkit.broadcastMessage("Remaining time until next update: " + remainingTime);
            }
        }.runTaskTimerAsynchronously(main, 0L, 20L);
    }

    @Override
    public void run() {
        topPlayerExpCache.updateTopPlayers();
        logger.info("UPDATED updateCachedTopPlayers");
    }

    public static String getTimeUntilNextUpdate() {
        long currentTime = System.currentTimeMillis();
        long nextUpdate = currentTime + fiveSeconds;

        long difference =  nextUpdate - currentTime;

        long minutes = (difference / 1000) / 60;
        long seconds = (difference / 1000) % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

}
