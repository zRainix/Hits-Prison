package de.hits.prison.pickaxe.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.base.model.dao.PlayerCellsGivingDao;
import de.hits.prison.base.model.entity.PlayerCellsGiving;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.entity.Player;

import java.util.List;

@Component
public class CellValueCommand extends AdvancedCommand {

    @Autowired
    private static PlayerCellsGivingDao playerCellsGivingDao;

    public CellValueCommand() {
        super("cellvalue");
    }

    @BaseCommand
    public void getCellValue(Player player) {
        List<PlayerCellsGiving> targetValue = playerCellsGivingDao.findByPlayer(player);

        MessageUtil.sendMessage(player, "§7Cell Value: §6" + targetValue + "&7.");
    }
}
