package de.hits.prison.playerList.scheduler;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.scheduler.anno.Scheduler;
import de.hits.prison.base.scheduler.helper.CustomScheduler;
import de.hits.prison.playerList.fileUtil.PlayerListUtil;
import de.hits.prison.server.placeholder.PlaceholderHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Scheduler(autoStart = false)
public class PlayerListScheduler extends CustomScheduler {

    @Autowired
    private static PlayerListUtil playerListUtil;
    @Autowired
    private static PlaceholderHelper placeholderHelper;

    public PlayerListScheduler() {
        super(20, 20);
    }

    @Override
    public void run() {
        List<String> header = playerListUtil.getHeader().stream().map(PlayerListUtil.AnimatedLines::update).map(PlayerListUtil.AnimatedLine::getText).map(placeholderHelper::replace).collect(Collectors.toList());
        List<String> footer = playerListUtil.getFooter().stream().map(PlayerListUtil.AnimatedLines::update).map(PlayerListUtil.AnimatedLine::getText).map(placeholderHelper::replace).collect(Collectors.toList());
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setPlayerListHeaderFooter(String.join("\n", header), String.join("\n", footer));
        }
    }
}
