package de.hits.prison.mine.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.world.WorldInitEvent;

@Component
public class MineWorldListener implements Listener {

    @Autowired
    private static MineHelper mineHelper;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void worldInit(WorldInitEvent e) {
        e.getWorld().setKeepSpawnInMemory(false);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        World world = e.getEntity().getWorld();

        if (mineHelper.getMineWorld(world) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        World world = e.getEntity().getWorld();
        MineWorld mineWorld = mineHelper.getMineWorld(world);
        if (mineWorld != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        World world = e.getEntity().getWorld();
        MineWorld mineWorld = mineHelper.getMineWorld(world);
        if (mineWorld != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByBlockEvent e) {
        World world = e.getEntity().getWorld();
        MineWorld mineWorld = mineHelper.getMineWorld(world);
        if (mineWorld != null) {
            e.setCancelled(true);
        }
    }
}
