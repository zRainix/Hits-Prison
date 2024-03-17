package de.hits.prison.scoreboard.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.scoreboard.fileUtil.ScoreboardUtil;
import de.hits.prison.server.placeholder.PlayerScoreboard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ScoreboardHelper {

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static ScoreboardUtil scoreboardUtil;

    private final HashMap<String, PlayerScoreboard> playerScoreboards;

    public ScoreboardHelper() {
        this.playerScoreboards = new HashMap<>();
    }

    public PlayerScoreboard getCurrentPlayerScoreboard(Player player) {
        String uuid = player.getUniqueId().toString();
        if (playerScoreboards.containsKey(uuid))
            return playerScoreboards.get(uuid);

        return getDefaultScoreboard(player);
    }

    public PlayerScoreboard getDefaultScoreboard(Player player) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
        if (prisonPlayer == null)
            return null;
        return new PlayerScoreboard(prisonPlayer, scoreboardUtil.getMainPrisonScoreboard());
    }

    public HashMap<String, PlayerScoreboard> getPlayerScoreboards() {
        return playerScoreboards;
    }

    public Map<String, PlayerScoreboard> getPlayerScoreboards(String scoreboardName) {
        return playerScoreboards.entrySet().stream().filter(e -> e.getValue().getPrisonScoreboard().getName().equals(scoreboardName)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void updatePlayerScoreboard(Player player, ScoreboardUtil.PrisonScoreboard prisonScoreboard) {
        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);
        if (prisonPlayer == null)
            return;

        String uuid = player.getUniqueId().toString();

        playerScoreboards.remove(uuid);
        PlayerScoreboard playerScoreboard = new PlayerScoreboard(prisonPlayer, prisonScoreboard);
        playerScoreboards.put(uuid, playerScoreboard);
        player.setScoreboard(playerScoreboard.getScoreboard());
    }

}
