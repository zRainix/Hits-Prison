package de.hits.prison.pickaxe.screen;

import de.hits.prison.base.util.ItemBuilder;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.screen.helper.ClickAction;
import de.hits.prison.server.screen.helper.Screen;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@Component
public class PickaxeUpdateScreen extends Screen {

    private Player player;

    public PickaxeUpdateScreen(Player player) {
        super(getTitle(player), 6);
        this.player = player;
    }

    @Override
    public void init() {
        super.init();
        addItem(new ItemBuilder(Material.PINK_CONCRETE).setDisplayName("§aHallo!").build(), new ClickAction() {
            @Override
            public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                player.sendMessage("§aHewwo :3333");
                player.closeInventory();
            }
        });
    }

    private static String getTitle(Player player) {
        return "§aPickaxe §6§l" + player.getName();
    }
}
