package de.hits.prison.cell.command;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.BaseCommand;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.base.model.dao.CellPlayerDao;
import de.hits.prison.base.model.entity.Cell;
import de.hits.prison.base.model.entity.CellPlayer;
import de.hits.prison.cell.helper.CellHelper;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Component
public class CellCommand extends AdvancedCommand {

    @Autowired
    private static CellPlayerDao cellPlayerDao;
    @Autowired
    private static CellHelper cellHelper;

    public CellCommand() {
        super("cell");
    }

    @BaseCommand
    public void cellHelp(Player player) {
        MessageUtil.sendMessage(player, "§7/cell help§c!");
    }

    @SubCommand(value = "help")
    public void getHelp(CommandSender sender) {
        MessageUtil.sendMessage(sender, "/cell create <Name>");
        MessageUtil.sendMessage(sender, "/cell disband");
        MessageUtil.sendMessage(sender, "/cell join <Name>");
        MessageUtil.sendMessage(sender, "/cell leave");
        MessageUtil.sendMessage(sender, "/cell promote <Name>");
        MessageUtil.sendMessage(sender, "/cell demote <Name>");
        MessageUtil.sendMessage(sender, "/cell kick <Name>");
        MessageUtil.sendMessage(sender, "/cell deposit <Type> <Value>");
        MessageUtil.sendMessage(sender, "/cell upgrade");
        MessageUtil.sendMessage(sender, "/cell setleader <Name>");
        MessageUtil.sendMessage(sender, "/cell info <Name>");
        MessageUtil.sendMessage(sender, "/celltop");

    }

    @SubCommand(value = "create")
    public void createCell(Player player) {

        CellPlayer cellPlayer = cellPlayerDao.findByUuid(player.getUniqueId());

        if(cellPlayer == null) {
            return;
        }

        Cell cell = cellHelper.getCell(player);
        if(cell != null) {
            MessageUtil.sendMessage(player, "§7You are already part of the Cell: " + cell.getName());
            return;
        }
        MessageUtil.sendMessage(player, "Wanna create a Cell?");
    }

}
