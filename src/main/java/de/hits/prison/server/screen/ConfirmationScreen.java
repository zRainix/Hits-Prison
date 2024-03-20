package de.hits.prison.server.screen;

import de.hits.prison.base.screen.helper.ClickAction;
import de.hits.prison.base.screen.helper.Screen;
import de.hits.prison.server.util.FireworkStarBuilder;
import de.hits.prison.server.util.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfirmationScreen extends Screen {

    ItemStack previewItem;
    ClickAction confirmAction;

    public ConfirmationScreen(ItemStack previewItem, ClickAction confirmAction, Screen parent) {
        super("§aConfirm", 3, parent);
        this.previewItem = previewItem;
        this.confirmAction = confirmAction;
    }

    @Override
    protected void init() {
        setItem(2 + 9, new FireworkStarBuilder().setColor(Color.RED).setDisplayName("§cDECLINE").addLore("§7Click to §cdecline").setAllItemFlags().build(), new ClickAction() {
            @Override
            public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                screenManager.openScreen(player, screen.getParent());
            }
        });

        setItem(4 + 9, previewItem, null);

        setItem(6 + 9, new FireworkStarBuilder().setColor(Color.GREEN).setDisplayName("§aCONFIRM").addLore("§7Click to §aconfirm").setAllItemFlags().build(), confirmAction);
    }

    public ItemStack getPreviewItem() {
        return previewItem;
    }

    public void setPreviewItem(ItemStack previewItem) {
        this.previewItem = previewItem;
    }

    public ClickAction getConfirmAction() {
        return confirmAction;
    }

    public void setConfirmAction(ClickAction confirmAction) {
        this.confirmAction = confirmAction;
    }
}
