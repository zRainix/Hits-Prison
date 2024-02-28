package de.hits.prison.scoreboard;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.scoreboard.command.ScoreboardCommand;
import de.hits.prison.scoreboard.fileUtil.ScoreboardUtil;
import de.hits.prison.scoreboard.helper.ScoreboardHelper;
import de.hits.prison.scoreboard.listener.ScoreboardListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.PluginManager;

@Component
public class ScoreboardManager implements Manager {

    @Autowired
    private static ScoreboardHelper scoreboardHelper;
    @Autowired
    private static ScoreboardUtil scoreboardUtil;

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        AutowiredManager.register(new ScoreboardHelper());

        // Commands
        hitsPrison.registerCommand(new ScoreboardCommand());

        // Listener
        pluginManager.registerEvents(new ScoreboardListener(), hitsPrison);

        for (Player player : Bukkit.getOnlinePlayers()) {
            scoreboardHelper.updatePlayerScoreboard(player, scoreboardUtil.getMainPrisonScoreboard());
        }
    }

    @Override
    public EventPriority getPriority() {
        return EventPriority.LOW;
    }
}
