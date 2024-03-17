package de.hits.prison.scoreboard.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.scoreboard.helper.ScoreboardHelper;
import de.hits.prison.server.placeholder.PlayerScoreboard;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Component
public class ScoreboardCommand extends AdvancedCommand {

    private static final String RESET_SCOREBOARD_PERMISSION = "prison.scoreboard.reset";

    @Autowired
    private static ScoreboardHelper scoreboardHelper;

    public ScoreboardCommand() {
        super("scoreboard");
    }

    @SubCommand(value = "reload", permission = RESET_SCOREBOARD_PERMISSION)
    public void reloadScoreboard(CommandSender sender, @CommandParameter(name = "players") List<Player> players) {
        int count = 0;
        for (Player player : players) {
            PlayerScoreboard playerScoreboard = scoreboardHelper.getCurrentPlayerScoreboard(player);
            if (playerScoreboard == null)
                continue;
            playerScoreboard.reload();
            count++;
        }
        MessageUtil.sendMessage(sender, "§7Reloaded §b" + count + " §7scoreboard(s).");
    }

}
