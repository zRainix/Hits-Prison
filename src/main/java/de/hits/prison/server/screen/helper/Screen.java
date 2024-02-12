package de.hits.prison.server.screen.helper;

import de.hits.prison.server.screen.ScreenManager;
import de.hits.prison.base.util.ItemBuilder;
import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@Component
public class Screen {

    @Autowired
    private static ScreenManager screenManager;

    private Inventory inventory;
    private HashMap<String, Object> data;
    private HashMap<Integer, ClickAction> clickActions;

    private String title;
    private boolean border;
    private int rows;
    private Screen parent;

    public Screen(String title, int rows) {
        this(title, rows, null);
    }

    public Screen(String title, int rows, Screen parent) {
        this.inventory = Bukkit.createInventory(null, 9 * rows, title);
        this.data = new HashMap<>();
        this.clickActions = new HashMap<>();
        this.border = true;
        this.title = title;
        this.rows = rows;
        this.parent = parent;
        init();
    }

    public void init() {
        this.inventory.clear();
        fillInventoryWithPlaceholder(this.inventory);
        if (this.parent != null) {
            ItemStack returnItemStack = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplayName("§cReturn to: §7" + parent.getTitle()).build();
            setItem(8, returnItemStack, new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    screenManager.openScreen(player, screen.getParent());
                }
            });
        }
    }

    private void fillInventoryWithPlaceholder(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null || inventory.getItem(i).getType().isAir()) {
                inventory.setItem(i, screenManager.getPlaceHolderItemStack());
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public void setDataValue(String key, Object value) {
        if (this.data.containsKey(key)) {
            this.data.remove(key);
        }
        this.data.put(key, value);
    }

    public Object getDataValue(String key) {
        if (this.data.containsKey(key)) {
            return this.data.get(key);
        }
        return null;
    }

    public int getRows() {
        return rows;
    }

    public Screen getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }

    public void setBorder(boolean border) {
        this.border = border;
    }

    public boolean isBorder() {
        return border;
    }

    public void setItem(int slot, ItemStack itemStack, ClickAction clickAction) {
        this.inventory.setItem(slot, itemStack);
        if (clickActions.containsKey(slot)) {
            clickActions.remove(slot);
        }
        if (clickAction != null) {
            clickActions.put(slot, clickAction);
        }
    }

    public void addItem(ItemStack itemStack, ClickAction clickAction) {
        for (int y = (border ? 1 : 0); y < (border ? (rows - 1) : rows); y++) {
            for (int x = (border ? 1 : 0); x < (border ? 8 : 9); x++) {
                int slot = x + (9 * y);
                if (inventory.getItem(slot).equals(screenManager.getPlaceHolderItemStack())) {
                    setItem(slot, itemStack, clickAction);
                    return;
                }
            }
        }
    }

    public void inventoryClicked(Player player, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (clickActions.containsKey(slot)) {
            clickActions.get(slot).onClick(player, this, e);
        }
    }
}
