package de.hits.prison.mechanic.pickaxe.listener;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.mechanic.pickaxe.helper.PickaxeHelper;
import de.hits.prison.model.entity.PrisonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.net.http.WebSocket;

public class PickaxeFlagsListener implements Listener {

    @Autowired
    private static PickaxeHelper pickaxeHelper;

    @Autowired
    private static PrisonPlayer prisonPlayer;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(prisonPlayer == null) {
            ItemStack playerpickaxe = pickaxeHelper.buildPlayerPickaxe(player);

            player.getInventory().addItem(playerpickaxe);

            pickaxeHelper.checkPlayerPickaxe(player);
            player.sendMessage("there you go buddy hehe");
        }
    }

    @EventHandler
    public void onDrop() {

    }

    public void onUsage() {

    }
}
