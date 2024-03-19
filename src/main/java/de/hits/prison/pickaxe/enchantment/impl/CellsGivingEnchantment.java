package de.hits.prison.pickaxe.enchantment.impl;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerCellsGivingDao;
import de.hits.prison.base.model.entity.PlayerCellsGiving;
import de.hits.prison.base.model.entity.PlayerEnchantment;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.helper.MineWorld;
import de.hits.prison.pickaxe.enchantment.anno.DefaultEnchantment;
import de.hits.prison.pickaxe.enchantment.helper.PickaxeEnchantmentImpl;
import de.hits.prison.pickaxe.fileUtil.CellsGivingUtil;
import de.hits.prison.pickaxe.fileUtil.KeyFinderUtil;
import de.hits.prison.pickaxe.fileUtil.PickaxeUtil;
import de.hits.prison.pickaxe.helper.PlayerDrops;
import de.hits.prison.server.util.ItemBuilder;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@Component
@DefaultEnchantment(maxLevel = 100, description = "Possibility to find Cell Value", activationPrice = "50000", priceMultiplier = "1.1", type = "Drops")
public class CellsGivingEnchantment extends PickaxeEnchantmentImpl {

    @Autowired
    private static CellsGivingUtil cellsGivingUtil;
    @Autowired
    private static PickaxeUtil pickaxeUtil;
    @Autowired
    private static PlayerCellsGivingDao playerCellsGivingDao;

    private final Random random;

    public CellsGivingEnchantment() {
        super("CellsGiving");
        this.random = new Random();
    }

    @Override
    public PlayerDrops onBreak(PrisonPlayer prisonPlayer, PlayerDrops playerDrops, PlayerEnchantment cellsGivingEnchantment, MineWorld mineWorld, BlockBreakEvent event) {
        PickaxeUtil.PickaxeEnchantment pickaxeEnchantment = pickaxeUtil.getPickaxeEnchantment(cellsGivingEnchantment.getEnchantmentName());
        if(pickaxeEnchantment == null) {
            return null;
        }

        CellsGivingUtil.CellsGivingLevel cellsGivingLevel = cellsGivingUtil.getCellsGivingLevel(cellsGivingEnchantment.getEnchantmentLevel());
        if(cellsGivingLevel == null) {
            return null;
        }

        CellsGivingUtil.CellsGivingDrop drop = returnCellsGivingDrop(cellsGivingEnchantment, cellsGivingLevel);
        if(drop == null) {
            return null;
        }

        int amount = addCellsGivingItem(drop, prisonPlayer);

        PlayerCellsGiving playerCellsGiving = new PlayerCellsGivingDao().findByPrisonPlayerAndType(prisonPlayer, drop.getItemType());
        MessageUtil.sendMessage(event.getPlayer(), "§7You found §6" + (amount == 1 ? "" : amount + "x§7 ") + drop.getColorPrefix() + drop.getItemType() + " §7(§c" + playerCellsGiving.getAmount() + "§7)");

        return null;
    }

    public int addCellsGivingItem(CellsGivingUtil.CellsGivingDrop drop, PrisonPlayer prisonPlayer) {
        PlayerCellsGiving playerCellsGiving = playerCellsGivingDao.findByPrisonPlayerAndType(prisonPlayer, drop.getItemType());
        if(playerCellsGiving == null) {
            playerCellsGiving = new PlayerCellsGiving();
            playerCellsGiving.setCellsGivingItem(drop.getItemType());
            playerCellsGiving.setAmount(0L);
            playerCellsGiving.setRefPrisonPlayer(prisonPlayer);
            playerCellsGivingDao.save(playerCellsGiving);

        }

        ArrayList<Integer> amounts = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : drop.getAmountWeights().entrySet()) {
            for(int i = 0; i<entry.getValue(); i++) {
                amounts.add(entry.getKey());
            }
        }
        int amount = amounts.get(random.nextInt(amounts.size()));

        playerCellsGiving.setAmount(playerCellsGiving.getAmount() + amount);
        playerCellsGivingDao.save(playerCellsGiving);
        return amount;
    }

    public CellsGivingUtil.CellsGivingDrop returnCellsGivingDrop(PlayerEnchantment cellsGivingEnchantment, CellsGivingUtil.CellsGivingLevel level) {

        if(level == null) {
            return null;
        }
        for(CellsGivingUtil.CellsGivingDropChance dropChance : level.getDrops()) {
            BigDecimal chance = dropChance.getChance();
            BigDecimal randomNumber = BigDecimal.valueOf(random.nextFloat());
            if(randomNumber.compareTo(chance) >= 0) {
                continue;
            }
            return dropChance.getDrop();
        }

        return null;
    }
}
