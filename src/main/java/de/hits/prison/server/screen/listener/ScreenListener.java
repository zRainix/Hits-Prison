package de.hits.prison.server.screen.listener;

import de.hits.prison.server.screen.ScreenManager;
import de.hits.prison.server.screen.helper.Screen;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Component
public class ScreenListener implements Listener {

    @Autowired
    private static ScreenManager screenManager;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) e.getWhoClicked();

        Screen screen = screenManager.getCurrentScreen(player);

        if (screen == null)
            return;

        if (screen.getInventory().getViewers().size() == 0) {
            screenManager.closeScreen(player);
            return;
        }
        e.setCancelled(true);
        if (e.getClickedInventory() != null && e.getClickedInventory() == screen.getInventory()) {
            screen.inventoryClicked(player, e);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        screenManager.closeScreen(player);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        if (player.getOpenInventory() == player.getInventory()) {
            screenManager.closeScreen(player);
        }
    }

}
