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

    protected CellsGivingEnchantment() {
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

        //returnCellsGivingDrop(cellsGivingEnchantment, cellsGivingLevel);
        addCellsGivingItem(drop, prisonPlayer);

        PlayerCellsGiving playerCellsGiving = new PlayerCellsGivingDao().findByPrisonPlayerAndType(prisonPlayer, drop.getItemType());
        MessageUtil.sendMessage(event.getPlayer(), "§7you found §b" + playerCellsGiving.getCellsGivingItem() + " (§c" + playerCellsGiving.getAmount() + "§b)");

        return null;
    }

    public void addCellsGivingItem(CellsGivingUtil.CellsGivingDrop drop, PrisonPlayer prisonPlayer) {
        PlayerCellsGiving playerCellsGiving = playerCellsGivingDao.findByPrisonPlayerAndType(prisonPlayer, drop.getItemType());
        if(playerCellsGiving == null) {
            playerCellsGiving = new PlayerCellsGiving();
            playerCellsGiving.setCellsGivingItem(drop.getItemType());
            playerCellsGiving.setAmount(0L);
            playerCellsGiving.setRefPrisonPlayer(prisonPlayer);
            playerCellsGivingDao.save(playerCellsGiving);
        }

        playerCellsGiving.setAmount(playerCellsGiving.getAmount() + drop.getAmount());
        new PlayerCellsGivingDao().save(playerCellsGiving);
    }

    public CellsGivingUtil.CellsGivingDrop returnCellsGivingDrop(PlayerEnchantment cellsGivingEnchantment, CellsGivingUtil.CellsGivingLevel level) {

        if(level == null) {
            return null;
        }
        for(CellsGivingUtil.CellsGivingDrop drop : level.getDrops()) {
            System.out.println(drop.getItemType());
            BigDecimal chance = drop.getChance();
            BigDecimal randomNumber = BigDecimal.valueOf(random.nextFloat());
            System.out.println(chance);
            System.out.println(randomNumber);
            System.out.println(chance.compareTo(randomNumber));
            if(randomNumber.compareTo(chance) >= 0) {
                continue;
            }
            return drop;
        }

        return null;
    }
}
