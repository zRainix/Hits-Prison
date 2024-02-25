package de.hits.prison.pickaxe.enchantment.listener;

import de.hits.prison.pickaxe.blocks.BlockValue;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImplManager;
import de.hits.prison.pickaxe.fileUtil.BlockValueUtil;
import de.hits.prison.pickaxe.helper.PickaxeHelper;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerEnchantmentDao;
import de.hits.prison.base.model.dao.PrisonPlayerDao;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class BlockBreakListener implements Listener {

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
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null)
            return;

        if (!pickaxeHelper.isCustomPickaxe(player.getInventory().getItemInMainHand(), prisonPlayer))
            return;

        // TODO: Check if player is in mine

        e.setDropItems(false);
        e.setExpToDrop(0);

        // TODO: Read from config "BlockValue.yml" file to give currency instead of set values

        List<PlayerEnchantment> playerEnchantments = prisonPlayer.getPlayerEnchantments();

        BlockValueUtil blockValueUtil = new BlockValueUtil();
        blockValueUtil.load();

        PlayerDrops playerDrops = new PlayerDrops();

        BlockValue blockValue = blockValueUtil.getBlockValue(e.getBlock().getType());
        if(blockValue != null) {
            playerDrops = PlayerDrops.generate(blockValue);
        }

        List<PlayerDrops> extraPlayerDrops = new ArrayList<>();

        PlayerDrops finalPlayerDrops = playerDrops;
        playerEnchantments.forEach(playerEnchantment -> {
            pickaxeEnchantmentImplManager.getEnchantmentsImplementations().stream().filter(pickaxeEnchantmentImpl -> pickaxeEnchantmentImpl.getEnchantmentName().equals(playerEnchantment.getEnchantmentName())).forEach(pickaxeEnchantmentImpl -> {
                PlayerDrops extraDrop = pickaxeEnchantmentImpl.onBreak(prisonPlayer, finalPlayerDrops.clonePlayerDrops(), playerEnchantment, e);
                if (extraDrop != null) {
                    extraPlayerDrops.add(extraDrop);
                }
            });
        });

        playerDrops.addAll(extraPlayerDrops);

        playerDrops.grantPlayer(player);
    }

    public void executeJackhammerEnchantment(Player player, PlayerEnchantment blockEnchantment, BlockBreakEvent e) {
        int mineWidth = 10, mineHeight = 12;

        int maxSize = Math.max(mineWidth, mineHeight);

        int mineX1 = 10;
        int mineX2 = 20;
        int mineZ1 = 10;
        int mineZ2 = 20;

        int blockX = e.getBlock().getX();
        int blockZ = e.getBlock().getZ();

        for (int offsetX = -maxSize; offsetX <= maxSize; offsetX++) {
            for (int offsetZ = -maxSize; offsetZ <= maxSize; offsetZ++) {
                int x = blockX + offsetX;
                int z = blockZ + offsetZ;

                if (!(x >= mineX1 && x <= mineX2) || !(z >= mineZ1 && z <= mineZ2)) {
                    continue;
                }

                if (!(offsetX == 0 && offsetZ == 0)) {
                    Block offset = e.getBlock().getRelative(offsetX, 0, offsetZ);
                    if (offset.getType().isAir())
                        continue;
                    if (offset.getType() == Material.BEDROCK)
                        continue;
                    offset.setType(Material.AIR);
                }

            }
        }
    }


}
