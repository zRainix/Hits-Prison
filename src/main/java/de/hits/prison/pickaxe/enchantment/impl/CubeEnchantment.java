package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.pickaxe.blocks.BlockValue;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.fileUtil.BlockValueUtil;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@DefaultEnchantment(maxLevel = 3, description = "Mines a cube of blocks around mined block. \nEvery extra minded block drops exp.", activationPrice = "100", priceMultiplier = "10", type = "Mining")
public class CubeEnchantment extends PickaxeEnchantmentImpl {

    @Autowired
    private static BlockValueUtil blockValueUtil;

    private static final HashMap<String, DropFocus> dropFocusHashMap = new HashMap<>();

    public CubeEnchantment() {
        super("Cube");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment cubeEnchantment, BlockBreakEvent event) {
        int level = cubeEnchantment.getEnchantmentLevel();

        PlayerDrops extraPlayerDrops = new PlayerDrops();

        for (int offsetX = -level; offsetX <= level; offsetX++) {
            for (int offsetY = -level; offsetY <= level; offsetY++) {
                for (int offsetZ = -level; offsetZ <= level; offsetZ++) {
                    if (!(offsetX == 0 && offsetY == 0 && offsetZ == 0)) {
                        Block offset = event.getBlock().getRelative(offsetX, offsetY, offsetZ);
                        if (offset.getType().isAir())
                            continue;
                        if (offset.getType().getHardness() == -1.0F)
                            continue;
                        if (offset.isLiquid())
                            continue;

                        extraPlayerDrops.add(randomPlayerDropsForCube(prisonPlayer, offset.getType()));
                        offset.setType(Material.AIR);
                    }
                }
            }
        }
        return extraPlayerDrops;
    }

    public PlayerDrops randomPlayerDropsForCube(PrisonPlayer prisonPlayer, Material material) {
        BlockValue blockValue = blockValueUtil.getBlockValue(material);
        String uuid = prisonPlayer.getPlayerUuid();
        if(blockValue == null) {
            return new PlayerDrops();
        }
        return PlayerDrops.generateRestricted(blockValue, dropFocusHashMap.getOrDefault(uuid, DropFocus.ASH));
    }

    @Override
    public void onRightClickAir(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment, PlayerInteractEvent e) {
        String uuid = prisonPlayer.getPlayerUuid();
        DropFocus dropFocus = dropFocusHashMap.getOrDefault(uuid, DropFocus.ASH);
        dropFocus = dropFocus.next(e.getPlayer());

        if(dropFocus == null) {
            MessageUtil.sendMessage(e.getPlayer(), "§7Can not switch DropFocus");
            return;
        }

        dropFocusHashMap.remove(uuid);
        dropFocusHashMap.put(uuid, dropFocus);
        MessageUtil.sendMessage(e.getPlayer(), "§7Switched DropFocus to " + dropFocus.getDisplayName());
    }

    public static enum DropFocus {

        ASH("§cVolcanic Ash", null),
        SHARDS("§bObsidian Shards", "enchantment.DropFocus.Cube.Shards"),
        EXP("§aExp", "enchantment.DropFocus.Cube.Exp");
        final String displayName;
        final String permission;
        DropFocus(String displayName, String permission) {
            this.displayName = displayName;
            this.permission = permission;
        }

        public String getDisplayName() {
            return displayName;
        }

        public BigInteger getValue(PlayerCurrency playerCurrency) {
            return switch(this) {
                case ASH -> playerCurrency.getVolcanicAsh();
                case SHARDS -> playerCurrency.getObsidianShards();
                case EXP -> playerCurrency.getExp();
            };
        }

        public DropFocus next(Player player) {
            List<DropFocus> availableDropFocus = Arrays.stream(values()).filter(dropFocus -> dropFocus.getPermission() == null || player.hasPermission(dropFocus.getPermission())).collect(Collectors.toList());

            for (int i = 0; i < availableDropFocus.size(); i++) {
                if(availableDropFocus.get(i) == this) {
                    DropFocus dropFocus = availableDropFocus.get((i+1)%availableDropFocus.size());
                    if(dropFocus != this)
                        return dropFocus;
                }
            }
            return null;
        }

        public String getPermission() {
            return permission;
        }
    }
}
