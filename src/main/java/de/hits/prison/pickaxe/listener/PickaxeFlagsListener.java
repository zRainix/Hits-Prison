package de.hits.prison.pickaxe.listener;

import de.hits.prison.pickaxe.screen.PickaxeUpdateScreen;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.server.screen.ScreenManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Component
public class PickaxeFlagsListener implements Listener {

    @Autowired
    private static PickaxeHelper pickaxeHelper;
    @Autowired
    private static ScreenManager screenManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        pickaxeHelper.checkPlayerPickaxe(player);

        screenManager.openScreen(player, new PickaxeUpdateScreen(player));
    }
}
