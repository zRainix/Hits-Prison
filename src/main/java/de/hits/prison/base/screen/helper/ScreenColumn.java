package de.hits.prison.base.screen.helper;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ScreenColumn {

    private final Logger logger = Bukkit.getLogger();

    private final Screen screen;
    private final int startRow;
    private final int startColumn;
    private final int width;

    private final List<Integer> slots;
    private final List<Entry> entries;

    public ScreenColumn(Screen screen, int startColumn, int startRow) {
        this(screen, startColumn, startRow, 1);
    }

    public ScreenColumn(Screen screen, int startColumn, int startRow, int width) {
        this.screen = screen;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.width = Math.max(width, 1);
        this.slots = new ArrayList<>();
        this.entries = new ArrayList<>();
        calculateSlots();
    }

    private void calculateSlots() {
        if (screen.isBorder() && (startRow == 0 || startColumn == 0 || startColumn == 8)) {
            logger.warning("ScreenColumn for screen " + screen.getTitle() + " starts inside border.");
        }
        int spaceDown = screen.getRows() - startRow - (screen.isBorder() ? 1 : 0);
        this.slots.clear();
        for (int rowOffset = 0; rowOffset < spaceDown; rowOffset++) {
            for (int colOffset = 0; colOffset < width; colOffset++) {
                this.slots.add((startRow + rowOffset) * 9 + ((startColumn + colOffset)));
            }
        }
    }

    public void addItem(ItemStack itemStack, ClickAction clickAction) {
        entries.add(new Entry(itemStack, clickAction));
    }

    public void removeItem(ItemStack itemStack) {
        entries.removeIf(entry -> entry.getItemStack() == itemStack);
    }

    public void initColumn() {
        int offset = screen.getOffset() * width;
        for (int i = offset; (i - offset) < slots.size() && i < entries.size(); i++) {
            Entry entry = entries.get(i);
            screen.setItem(slots.get(i - offset), entry.getItemStack(), entry.getClickAction());
        }
    }

    public int getMaxOffset() {
        int overflow = (entries.size() - slots.size());
        if(overflow <= 0)
            return 0;

        int offset = 1;

        while(overflow > offset * width) {
            offset++;
        }

        return Math.max(0, offset);
    }

    public void clear() {
        this.entries.clear();
    }
}
