package de.hits.prison.pickaxe.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.*;

@Component
public class PickaxeFlagsListener implements Listener {

    @Autowired
    private static PickaxeHelper pickaxeHelper;
    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        pickaxeHelper.checkPlayerInventory(player);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        ItemStack itemStack = event.getItemDrop().getItemStack();

        if (!pickaxeHelper.isCustomPickaxe(itemStack, null))
            return;

        if (pickaxeHelper.isCustomPickaxe(itemStack, prisonPlayer))
            event.setCancelled(true);
        else
            itemStack.setType(Material.AIR);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        event.getDrops().forEach(itemStack -> {
            if (pickaxeHelper.isCustomPickaxe(itemStack, null))
                itemStack.setType(Material.AIR);
        });
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        pickaxeHelper.checkPlayerInventory(player);
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        pickaxeHelper.checkPlayerInventory(player);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        CraftingInventory craftingInventory = event.getInventory();
        for (int i = 0; i < craftingInventory.getMatrix().length; i++) {
            ItemStack itemStack = craftingInventory.getMatrix()[i];
            if (!pickaxeHelper.isCustomPickaxe(itemStack, null))
                continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvilInventory = event.getInventory();
        for (int i = 0; i < anvilInventory.getContents().length; i++) {
            ItemStack itemStack = anvilInventory.getContents()[i];
            if (!pickaxeHelper.isCustomPickaxe(itemStack, null))
                continue;
            event.setResult(null);
            return;
        }
    }

    @EventHandler
    public void onSmith(SmithItemEvent event) {
        SmithingInventory smithingInventory = event.getInventory();
        for (int i = 0; i < smithingInventory.getContents().length; i++) {
            ItemStack itemStack = smithingInventory.getContents()[i];
            if (!pickaxeHelper.isCustomPickaxe(itemStack, null))
                continue;
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onMoveToNotPlayerInventory(InventoryClickEvent event) {
        boolean shiftClick = event.getClick().isShiftClick();
        Inventory clicked = event.getClickedInventory();
        if (shiftClick == (clicked == event.getWhoClicked().getInventory())) {
            ItemStack clickedOn = shiftClick ? event.getCurrentItem() : event.getCursor();
            if (clickedOn != null && pickaxeHelper.isCustomPickaxe(clickedOn, null)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        ItemStack dragged = event.getOldCursor();

        if (pickaxeHelper.isCustomPickaxe(dragged, null)) {
            int inventorySize = event.getInventory().getSize();

            for (int i : event.getRawSlots()) {
                if (i < inventorySize) {
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }
}
