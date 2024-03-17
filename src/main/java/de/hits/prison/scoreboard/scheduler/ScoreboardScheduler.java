package de.hits.prison.scoreboard.scheduler;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.fileUtil.helper.AnimateLineFileUtil;
import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import de.hits.prison.scoreboard.fileUtil.ScoreboardUtil;
import de.hits.prison.scoreboard.helper.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ScoreboardScheduler extends CustomScheduler {

    @Autowired
    private static ScoreboardHelper scoreboardHelper;

    private ScoreboardUtil.PrisonScoreboard prisonScoreboard;

    public ScoreboardScheduler(ScoreboardUtil.PrisonScoreboard prisonScoreboard) {
        super(prisonScoreboard.getUpdatePeriod(), prisonScoreboard.getUpdatePeriod());
        this.prisonScoreboard = prisonScoreboard;
    }

    public void update(ScoreboardUtil.PrisonScoreboard prisonScoreboard) {
        this.prisonScoreboard = prisonScoreboard;
        setDelay(0);
        setPeriod(prisonScoreboard.getUpdatePeriod());
        stop();
        start();
    }

    @Override
    public void run() {
        prisonScoreboard.getRows().forEach(AnimateLineFileUtil.AnimatedLines::update);

        scoreboardHelper.getPlayerScoreboards(this.prisonScoreboard.getName()).forEach((s, playerScoreboard) -> {
            Player player = Bukkit.getPlayer(UUID.fromString(s));
            if (player != null)
                player.setScoreboard(playerScoreboard.getScoreboard());
            playerScoreboard.update();
        });
    }
}
