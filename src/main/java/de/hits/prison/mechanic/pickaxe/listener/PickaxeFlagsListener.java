package de.hits.prison.mechanic.pickaxe.listener;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.pickaxe.helper.PickaxeHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Component
public class PickaxeFlagsListener implements Listener {

    @Autowired
    private static PickaxeHelper pickaxeHelper;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        pickaxeHelper.checkPlayerPickaxe(player);
    }
}
