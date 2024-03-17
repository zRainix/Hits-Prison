package de.hits.prison.mine.listener;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerEnchantmentDao;
import de.hits.prison.base.model.dao.PlayerMineDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.MineTrustedPlayer;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineHelper;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImplManager;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class RightClickAirListener implements Listener {

    @Autowired
    private static Logger logger;

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;
    @Autowired
    private static PickaxeEnchantmentImplManager pickaxeEnchantmentImplManager;
    @Autowired
    private static PickaxeHelper pickaxeHelper;
    @Autowired
    private static MineHelper mineHelper;
    @Autowired
    private static PlayerMineDao playerMineDao;

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        MineWorld mineWorld = mineHelper.getMineWorld(e.getPlayer().getWorld());

        if (mineWorld == null)
            return;

        if (!pickaxeHelper.isCustomPickaxe(player.getInventory().getItemInMainHand(), prisonPlayer)) {
            e.setCancelled(true);
            return;
        }

        if(e.getAction() != Action.RIGHT_CLICK_AIR)
            return;

        PlayerMine playerMine = playerMineDao.findByPrisonPlayer(mineWorld.getPrisonPlayer());

        if (!Objects.equals(mineWorld.getPrisonPlayer().getId(), prisonPlayer.getId())) {
            boolean allowed = false;

            for (MineTrustedPlayer trustedPlayer : playerMine.getTrustedPlayers()) {
                if (Objects.equals(trustedPlayer.getRefTrustedPrisonPlayer().getId(), prisonPlayer.getId())) {
                    allowed = true;
                    break;
                }
            }

            if (!allowed) {
                MessageUtil.sendMessage(player, "§cYou are not a trusted player.");
                e.setCancelled(true);
                return;
            }
        }

        e.setCancelled(true);

        List<PlayerEnchantment> playerEnchantments = prisonPlayer.getPlayerEnchantments();

        playerEnchantments.forEach(playerEnchantment -> {
            pickaxeEnchantmentImplManager.getEnchantmentsImplementations().stream().filter(pickaxeEnchantmentImpl -> pickaxeEnchantmentImpl.getEnchantmentName().equals(playerEnchantment.getEnchantmentName())).forEach(pickaxeEnchantmentImpl -> {
                pickaxeEnchantmentImpl.onRightClickAir(prisonPlayer, playerEnchantment, mineWorld, e);
            });
        });
    }


}
