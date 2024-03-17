package de.hits.prison.mine.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.mine.helper.MineHelper;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;

@Component
public class TemplateWorldListener implements Listener {

    @Autowired
    private static MineHelper mineHelper;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void worldInit(WorldInitEvent e) {
        e.getWorld().setKeepSpawnInMemory(false);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByBlockEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        World world = e.getBlock().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(EntityInteractEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        World world = e.getPlayer().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        World world = e.getPlayer().getWorld();
        if (mineHelper.isTemplateWorld(world)) {
            e.setCancelled(true);
        }
    }
}
