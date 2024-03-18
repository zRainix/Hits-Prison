package de.hits.prison.mine.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.scoreboard.fileUtil.ScoreboardUtil;
import de.hits.prison.scoreboard.helper.ScoreboardHelper;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;

@Component
public class MineWorldListener implements Listener {

    @Autowired
    private static MineHelper mineHelper;
    @Autowired
    private static ScoreboardHelper scoreboardHelper;
    @Autowired
    private static ScoreboardUtil scoreboardUtil;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void worldInit(WorldInitEvent e) {
        e.getWorld().setKeepSpawnInMemory(false);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isMineWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isMineWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isMineWorld(world)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByBlockEvent e) {
        World world = e.getEntity().getWorld();
        if (mineHelper.isMineWorld(world)) {
            e.setCancelled(true);
        }
    }
}
