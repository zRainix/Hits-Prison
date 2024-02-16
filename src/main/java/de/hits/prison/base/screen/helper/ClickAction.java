package de.hits.prison.base.screen.helper;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class ClickAction {

    public abstract void onClick(Player player, Screen screen, InventoryClickEvent event);

}