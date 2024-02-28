package de.hits.prison.scoreboard.scheduler;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import de.hits.prison.scoreboard.helper.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Scheduler
public class ScoreboardScheduler extends CustomScheduler {

    @Autowired
    private static ScoreboardHelper scoreboardHelper;

    private static final long ONE_SECOND = 20L;

    public ScoreboardScheduler() {
        super(ONE_SECOND, ONE_SECOND);
    }

    @Override
    public void run() {
        scoreboardHelper.getPlayerScoreboards().forEach((s, playerScoreboard) -> {
            Player player = Bukkit.getPlayer(UUID.fromString(s));
            if (player != null)
                player.setScoreboard(playerScoreboard.getScoreboard());
            playerScoreboard.update();
        });
    }
}
