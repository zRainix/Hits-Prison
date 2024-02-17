package de.hits.prison.base.screen;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.helper.Manager;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.base.screen.listener.ScreenListener;
import de.hits.prison.server.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;

public class ScreenManager implements Manager {

    private ItemStack placeHolderItemStack;
    private HashMap<Player, Screen> playerScreen;

    @Override
    public void register(HitsPrison hitsPrison, PluginManager pluginManager) {
        ScreenListener screenListener = new ScreenListener();
        pluginManager.registerEvents(screenListener, hitsPrison);

        playerScreen = new HashMap<>();
        buildPlaceholder();
    }

    private void buildPlaceholder() {
        placeHolderItemStack = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName("§7").setAllItemFlags().build();
    }

    public ItemStack getPlaceHolderItemStack() {
        return placeHolderItemStack;
    }

    public Screen getCurrentScreen(Player player) {
        return playerScreen.getOrDefault(player, null);
    }

    public void openScreen(Player player, Screen screen) {
        closeScreen(player);
        if (screen == null) {
            player.closeInventory();
            return;
        }
        screen.initScreen();
        playerScreen.put(player, screen);
        player.openInventory(screen.getInventory());
    }

    public void closeScreen(Player player) {
        playerScreen.remove(player);
    }

    public void closeAllScreens() {
        for (Player player : playerScreen.keySet()) {
            player.closeInventory();
            closeScreen(player);
        }
    }
}
