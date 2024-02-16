package de.hits.prison.base.screen.helper;

import org.bukkit.inventory.ItemStack;

public class Entry {
    private ItemStack itemStack;
    private ClickAction clickAction;

    public Entry(ItemStack itemStack, ClickAction clickAction) {
        this.itemStack = itemStack;
        this.clickAction = clickAction;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickAction clickAction) {
        this.clickAction = clickAction;
    }
}