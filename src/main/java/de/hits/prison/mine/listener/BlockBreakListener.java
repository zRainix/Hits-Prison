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
import de.hits.prison.pickaxe.fileUtil.BlockValueUtil;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class BlockBreakListener implements Listener {

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
    @Autowired
    private static BlockValueUtil blockValueUtil;
    @Autowired
    private static PickaxeUtil pickaxeUtil;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        MineWorld mineWorld = mineHelper.getMineWorld(e.getBlock().getWorld());

        if (mineWorld == null)
            return;

        if (!pickaxeHelper.isCustomPickaxe(player.getInventory().getItemInMainHand(), prisonPlayer)) {
            e.setCancelled(true);
            return;
        }

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

        if (!mineWorld.isMineBlock(e.getBlock())) {
            e.setCancelled(true);
            return;
        }

        e.setDropItems(false);
        e.setExpToDrop(0);

        List<PlayerEnchantment> playerEnchantments = prisonPlayer.getPlayerEnchantments();

        PlayerDrops playerDrops = PlayerDrops.generate(blockValueUtil.getBlockValue(e.getBlock().getType()));

        List<PlayerDrops> extraPlayerDrops = new ArrayList<>();

        playerEnchantments.forEach(playerEnchantment -> {
            pickaxeEnchantmentImplManager.getEnchantmentsImplementations().stream().filter(pickaxeEnchantmentImpl -> pickaxeEnchantmentImpl.getEnchantmentName().equals(playerEnchantment.getEnchantmentName())).forEach(pickaxeEnchantmentImpl -> {
                PlayerDrops extraDrop = pickaxeEnchantmentImpl.onBreak(prisonPlayer, playerDrops.clonePlayerDrops(), playerEnchantment, mineWorld, e);
                if (extraDrop != null) {
                    extraPlayerDrops.add(extraDrop);
                }
            });
        });

        playerDrops.addAll(extraPlayerDrops);

        playerDrops.grantPlayer(player);
    }
}
