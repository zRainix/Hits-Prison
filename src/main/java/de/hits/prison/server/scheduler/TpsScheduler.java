package de.hits.prison.server.scheduler;

import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

@Scheduler
public class TpsScheduler extends CustomScheduler {

    private static final long ONE_TICK = 1L;
    private static final int SIXTY_SECONDS_IN_TICKS = 20 * 60;

    private static double tps = 0D;

    private final List<Long> lastTicks;

    public TpsScheduler() {
        super(ONE_TICK, ONE_TICK);
        this.lastTicks = new ArrayList<>();
    }

    @Override
    public void run() {
        if(this.lastTicks.size() == SIXTY_SECONDS_IN_TICKS)
            this.lastTicks.remove(0);

        this.lastTicks.add(System.currentTimeMillis());

        int lastIndex = this.lastTicks.size() - 1;
        long firstTick = this.lastTicks.get(0);
        long lastTick = this.lastTicks.get(lastIndex);

        long diff = lastTick - firstTick;

        tps = ((double) (lastIndex) / (double) diff) * 1000D;
    }

    public static double getTps() {
        return tps;
    }
}
