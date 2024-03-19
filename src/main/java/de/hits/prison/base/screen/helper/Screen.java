package de.hits.prison.base.screen.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.screen.ScreenManager;
import de.hits.prison.server.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Component
public abstract class Screen {

    @Autowired
    protected static ScreenManager screenManager;

    private final Inventory inventory;
    private final HashMap<String, Object> data;
    private final HashMap<Integer, ClickAction> clickActions;
    private final List<ScreenColumn> screenColumns;

    private final String title;
    private final int rows;
    private final Screen parent;
    private final boolean border;
    private int offset;

    public Screen(String title, int rows) {
        this(title, rows, null);
    }

    public Screen(String title, int rows, Screen parent) {
        this(title, rows, true, parent);
    }

    public Screen(String title, int rows, boolean border, Screen parent) {
        this.inventory = Bukkit.createInventory(null, 9 * rows, title);
        this.data = new HashMap<>();
        this.clickActions = new HashMap<>();
        this.screenColumns = new ArrayList<>();
        this.title = title;
        this.rows = rows;
        this.parent = parent;
        this.border = border;
        this.offset = 0;
    }

    public void initScreen() {
        this.inventory.clear();
        this.clickActions.clear();
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
        for (ScreenColumn screenColumn : screenColumns) {
            screenColumn.clear();
        }
        init();
        initColumns();
    }

    protected abstract void init();

    protected void initColumns() {
        int maxOffset = 0;
        for (ScreenColumn screenColumn : screenColumns) {
            maxOffset = Math.max(maxOffset, screenColumn.getMaxOffset());
            screenColumn.initColumn();
        }

        if (maxOffset == 0)
            return;

        int lastRow = getRows() - 1;

        if (offset > 0) {
            setItem(8 + (lastRow - 1) * 9, new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).setDisplayName("§8Scroll up").build(), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    screen.offset--;
                    screen.initScreen();
                }
            });
        }
        if (offset < maxOffset) {
            setItem(8 + (lastRow) * 9, new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplayName("§8Scroll down").build(), new ClickAction() {
                @Override
                public void onClick(Player player, Screen screen, InventoryClickEvent event) {
                    screen.offset++;
                    screen.initScreen();
                }
            });
        }
    }

    private void fillInventoryWithPlaceholder(Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (itemStack == null || itemStack.getType().isAir()) {
                ItemStack placeHolder = screenManager.getPlaceHolderItemStack();
                if (border && (i % 9 == 0 || i % 9 == 8 || i / 9 == 0 || i / 9 == (inventory.getSize() - 1) / 9)) {
                    placeHolder = screenManager.getBorderItemStack();
                }
                inventory.setItem(i, placeHolder);
            }
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setDataValue(String key, Object value) {
        this.data.remove(key);
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

    public boolean isBorder() {
        return border;
    }

    public void setItem(int slot, ItemStack itemStack, ClickAction clickAction) {
        this.inventory.setItem(slot, itemStack);
        clickActions.remove(slot);
        if (clickAction != null) {
            clickActions.put(slot, clickAction);
        }
    }

    public void addItem(ItemStack itemStack, ClickAction clickAction) {
        for (int y = (border ? 1 : 0); y < (border ? (rows - 1) : rows); y++) {
            for (int x = (border ? 1 : 0); x < (border ? 8 : 9); x++) {
                int slot = x + (9 * y);
                if (Objects.equals(inventory.getItem(slot), screenManager.getPlaceHolderItemStack())) {
                    setItem(slot, itemStack, clickAction);
                    return;
                }
            }
        }
    }

    public int[] getCenteredSlot(int items) {
        return switch (items) {
            case 1 -> new int[]{4};
            case 2 -> new int[]{3, 5};
            case 3 -> new int[]{2, 4, 6};
            case 4 -> new int[]{1, 3, 5, 7};
            case 5 -> new int[]{2, 3, 4, 5, 6};
            case 6 -> new int[]{1, 2, 3, 5, 6, 7};
            case 7 -> new int[]{1, 2, 3, 4, 5, 6, 7};
            case 8 -> new int[]{0, 1, 2, 3, 5, 6, 7, 8};
            case 9 -> new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
            default -> throw new IllegalStateException("Unexpected value: " + items);
        };
    }

    public void addScreenColumn(ScreenColumn screenColumn) {
        this.screenColumns.add(screenColumn);
    }

    public void inventoryClicked(Player player, InventoryClickEvent e) {
        int slot = e.getSlot();
        if (clickActions.containsKey(slot)) {
            clickActions.get(slot).onClick(player, this, e);
        }
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
