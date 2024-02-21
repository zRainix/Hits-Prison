package de.hits.prison.pickaxe.enchantment.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerEnchantmentDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImplManager;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Component
public class RightClickAirListener implements Listener {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;
    @Autowired
    private static PickaxeEnchantmentImplManager pickaxeEnchantmentImplManager;
    @Autowired
    private static PickaxeHelper pickaxeHelper;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        if (!pickaxeHelper.isCustomPickaxe(player.getInventory().getItemInMainHand(), prisonPlayer))
            return;

        if(e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        e.setCancelled(true);

        List<PlayerEnchantment> playerEnchantments = prisonPlayer.getPlayerEnchantments();

        playerEnchantments.forEach(playerEnchantment -> {
            pickaxeEnchantmentImplManager.getEnchantmentsImplementations().stream().filter(pickaxeEnchantmentImpl -> pickaxeEnchantmentImpl.getEnchantmentName().equals(playerEnchantment.getEnchantmentName())).forEach(pickaxeEnchantmentImpl -> {
                pickaxeEnchantmentImpl.onRightClickAir(prisonPlayer, playerEnchantment, e);
            });
        });
    }


}
