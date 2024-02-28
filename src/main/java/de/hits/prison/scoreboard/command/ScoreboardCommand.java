package de.hits.prison.scoreboard.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.scoreboard.scheduler.ScoreboardScheduler;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.entity.Player;

@Component
public class ScoreboardCommand extends AdvancedCommand {

    @Autowired
    private static ScoreboardScheduler scoreboardScheduler;

    public ScoreboardCommand() {
        super("scoreboard");
    }

}
