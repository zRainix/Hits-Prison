package de.hits.prison.scoreboard.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.scoreboard.fileUtil.ScoreboardUtil;
import de.hits.prison.scoreboard.helper.ScoreboardHelper;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

@Component
public class ScoreboardListener implements Listener {

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static ScoreboardHelper scoreboardHelper;
    @Autowired
    private static ScoreboardUtil scoreboardUtil;
    @Autowired
    private static MineHelper mineHelper;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        scoreboardHelper.updatePlayerScoreboard(player, scoreboardUtil.getMainPrisonScoreboard());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = event.getPlayer().getWorld();
        MineWorld mineWorld;
        if ((mineWorld = mineHelper.getMineWorld(world)) != null) {
            scoreboardHelper.updatePlayerScoreboard(player, scoreboardUtil.getPrisonScoreboard(mineWorld.getPrisonPlayer().getPlayerUuid().equals(player.getUniqueId().toString()) ? "Mine" : "VisitMine"));
        } else {
            scoreboardHelper.updatePlayerScoreboard(player, scoreboardUtil.getMainPrisonScoreboard());
        }
    }

}
