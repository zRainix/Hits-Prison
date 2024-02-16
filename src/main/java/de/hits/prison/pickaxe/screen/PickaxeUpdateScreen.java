package de.hits.prison.pickaxe.screen;

import de.hits.prison.server.util.ItemBuilder;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.screen.helper.ClickAction;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.base.screen.helper.ScreenColumn;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@Component
public class PickaxeUpdateScreen extends Screen {

    private final Player player;
    private final ScreenColumn testColumn;
    private final ScreenColumn testColumn2;

    public PickaxeUpdateScreen(Player player) {
        super(getTitle(player), 6);
        this.player = player;
        this.testColumn = new ScreenColumn(this, 1, 1, 1);
        addScreenColumn(this.testColumn);
        this.testColumn2 = new ScreenColumn(this, 7, 1, 1);
        addScreenColumn(this.testColumn2);
    }

    @Override
    public void init() {
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            testColumn.addItem(new ItemBuilder(Material.values()[i + 201]).setDisplayName("§aHallo: " + (i + 1)).build(), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    player.sendMessage("§aHewwo :" + "3".repeat(finalI + 1));
                    player.closeInventory();
                }
            });
        }
        for (int i = 0; i < 12; i++) {
            int finalI = i;
            testColumn2.addItem(new ItemBuilder(Material.values()[i + 201]).setDisplayName("§aHallo 2: " + (i + 1)).build(), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    player.sendMessage("§6Hewwo :" + "3".repeat(finalI + 1));
                    player.closeInventory();
                }
            });
        }
    }

    private static String getTitle(Player player) {
        return "§aPickaxe §6§l" + player.getName();
    }

    public Player getPlayer() {
        return player;
    }
}
