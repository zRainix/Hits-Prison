package de.hits.prison.mechanic.pickaxe.listener;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.mechanic.pickaxe.anno.EnchantmentMethod;
import de.hits.prison.mechanic.pickaxe.helper.DropRate;
import de.hits.prison.mechanic.pickaxe.helper.enums.EnchantmentRarity;
import de.hits.prison.mechanic.pickaxe.helper.enums.EnchantmentType;
import de.hits.prison.model.dao.PlayerEnchantmentDao;
import de.hits.prison.model.dao.PrisonPlayerDao;
import de.hits.prison.model.entity.PlayerEnchantment;
import de.hits.prison.model.entity.PrisonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

@Component
public class BlockBreakListener implements Listener {

    private Logger logger = Bukkit.getLogger();

    @Autowired
    private static PrisonPlayerDao prisonPlayerDao;
    @Autowired
    private static PlayerEnchantmentDao playerEnchantmentDao;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        // TODO: Check if item used is pickaxe
        // TODO: Check if player is in mine

        PrisonPlayer prisonPlayer = prisonPlayerDao.findByPlayer(player);

        if (prisonPlayer == null) {
            return;
        }

        List<PlayerEnchantment> playerEnchantments = prisonPlayer.getPlayerEnchantments();

        for (PlayerEnchantment playerEnchantment : playerEnchantments) {
            executePlayerEnchantment(player, playerEnchantment, e);
        }

        playerBreakBlock(player, e.getBlock(), new DropRate(0.7, 0.3, 0.1));
    }

    public void playerBreakBlock(Player player, Block block, DropRate dropRate) {
        Random random = new Random();

        boolean ash = random.nextDouble() <= dropRate.getAshDropRate();
        boolean shards = random.nextDouble() <= dropRate.getShardsDropRate();
        boolean exp = random.nextDouble() <= dropRate.getExpDropRate();

        player.sendMessage("Ash:" + ash + ", Shards: " + shards + ", Exp: " + exp);
    }

    @EnchantmentMethod(enchantment = "Cube", enchantmentType = EnchantmentType.BREAK, enchantmentRarity = EnchantmentRarity.RARE)
    public void executeBlockEnchantment(Player player, PlayerEnchantment blockEnchantment, BlockBreakEvent e) {
        int level = blockEnchantment.getEnchantmentLevel();

        for (int offsetX = -level; offsetX <= level; offsetX++) {
            for (int offsetY = -level; offsetY <= level; offsetY++) {
                for (int offsetZ = -level; offsetZ <= level; offsetZ++) {
                    if (!(offsetX == 0 && offsetY == 0 && offsetZ == 0)) {
                        Block offset = e.getBlock().getRelative(offsetX, offsetY, offsetZ);
                        if (offset.getType().isAir())
                            continue;
                        if (offset.getType() == Material.BEDROCK)
                            continue;
                        offset.setType(Material.AIR);
                        playerBreakBlock(player, offset, new DropRate(0.8, 0, 0));
                    }
                }
            }
        }
    }

    @EnchantmentMethod(enchantment = "Jackhammer", enchantmentType = EnchantmentType.BREAK, enchantmentRarity = EnchantmentRarity.RARE)
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
                    playerBreakBlock(player, offset, new DropRate(0.8, 0, 0));
                }

            }
        }
    }

    public PlayerEnchantment getEnchantment(Player player, String enchantment) {
        return playerEnchantmentDao.findByPlayerAndEnchantmentName(player, enchantment);
    }

    private void executePlayerEnchantment(Player player, PlayerEnchantment playerEnchantment, BlockBreakEvent e) {
        for (Method method : getClass().getMethods()) {
            if (!method.isAnnotationPresent(EnchantmentMethod.class))
                continue;
            EnchantmentMethod enchantmentMethod = method.getAnnotation(EnchantmentMethod.class);
            if (!enchantmentMethod.enchantment().equals(playerEnchantment.getEnchantmentName()))
                continue;
            try {
                method.invoke(this, player, playerEnchantment, e);
            } catch (IllegalAccessException | InvocationTargetException ex) {
                logger.warning("Could not invoke method " + method.getName());
            }
        }
    }

}
