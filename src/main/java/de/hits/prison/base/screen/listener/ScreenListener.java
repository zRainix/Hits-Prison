package de.hits.prison.base.screen.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.screen.ScreenManager;
import de.hits.prison.base.screen.helper.Screen;
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
        if (!(e.getWhoClicked() instanceof Player player))
            return;

        Screen screen = screenManager.getCurrentScreen(player);

        if (screen == null)
            return;

        if (screen.getInventory().getViewers().isEmpty()) {
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

        Screen currentScreen = screenManager.getCurrentScreen(player);

        if (currentScreen == null)
            return;

        if (e.getInventory() == currentScreen.getInventory()) {
            screenManager.closeScreen(player);
        }
    }
}
