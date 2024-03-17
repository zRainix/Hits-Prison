package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.model.entity.PlayerCurrency;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.helper.PlayerDrops;
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

@DefaultEnchantment(maxLevel = 3, description = "Mines a cube of blocks around mined block. \nEvery extra minded block drops exp.", activationPrice = "100", priceMultiplier = "10", type = "Mining")
public class CubeEnchantment extends PickaxeEnchantmentImpl {

    private static final HashMap<String, DropFocus> dropFocusHashMap = new HashMap<>();

    public CubeEnchantment() {
        super("Cube");
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment cubeEnchantment, MineWorld mineWorld, BlockBreakEvent event) {
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
                        if (!mineWorld.isMineBlock(offset))
                            continue;
                        offset.setType(Material.AIR);
                        extraPlayerDrops.add(randomPlayerDropsForCube(prisonPlayer));
                    }
                }
            }
        }
        return extraPlayerDrops;
    }

    public PlayerDrops randomPlayerDropsForCube(PrisonPlayer prisonPlayer) {
        // TODO: Genaue Werte anpassen, ggf. aus Config auslesen.
        String uuid = prisonPlayer.getPlayerUuid();
        return switch (dropFocusHashMap.getOrDefault(uuid, DropFocus.ASH)) {
            case EXP -> PlayerDrops.generate(0, 0, 0, 0, 1, 5);
            case ASH -> PlayerDrops.generate(1, 5, 0, 0, 0, 0);
            case SHARDS -> PlayerDrops.generate(0, 0, 1, 5, 0, 0);
        };
    }

    @Override
    public void onRightClickAir(PrisonPlayer prisonPlayer, PlayerEnchantment playerEnchantment, MineWorld mineWorld, PlayerInteractEvent event) {
        String uuid = prisonPlayer.getPlayerUuid();
        DropFocus dropFocus = dropFocusHashMap.getOrDefault(uuid, DropFocus.ASH);
        dropFocus = dropFocus.next(event.getPlayer());

        if (dropFocus == null) {
            MessageUtil.sendMessage(event.getPlayer(), "§7Can not switch DropFocus");
            return;
        }

        dropFocusHashMap.remove(uuid);
        dropFocusHashMap.put(uuid, dropFocus);
        MessageUtil.sendMessage(event.getPlayer(), "§7Switched DropFocus to " + dropFocus.getDisplayName());
    }

    private static enum DropFocus {

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
            return switch (this) {
                case ASH -> playerCurrency.getVolcanicAsh();
                case SHARDS -> playerCurrency.getObsidianShards();
                case EXP -> playerCurrency.getExp();
            };
        }

        public DropFocus next(Player player) {
            List<DropFocus> availableDropFocus = Arrays.stream(values()).filter(dropFocus -> dropFocus.getPermission() == null || player.hasPermission(dropFocus.getPermission())).collect(Collectors.toList());

            for (int i = 0; i < availableDropFocus.size(); i++) {
                if (availableDropFocus.get(i) == this) {
                    return availableDropFocus.get((i + 1) % availableDropFocus.size());
                }
            }
            return null;
        }

        public String getPermission() {
            return permission;
        }
    }
}
