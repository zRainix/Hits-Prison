package de.hits.prison.base.command;

import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.command.anno.CommandParameter;
import de.hits.prison.server.command.anno.SubCommand;
import de.hits.prison.server.command.helper.AdvancedCommand;
import de.hits.prison.server.fileUtil.helper.FileUtil;
import de.hits.prison.server.fileUtil.helper.FileUtilManager;
import org.bukkit.command.CommandSender;

@Component
public class FileUtilCommand extends AdvancedCommand {

    @Autowired
    private static FileUtilManager fileUtilManager;

    public FileUtilCommand() {
        super("fileUtil");
    }

    @SubCommand(subCommand = "load")
    private void loadFile(CommandSender sender, @CommandParameter(name = "fileName") FileUtil fileUtil) {
        fileUtil.load();
        sender.sendMessage("§7File §6" + fileUtil.getFileName() + " §7loaded successfully.");
    }

    @SubCommand(subCommand = "save")
    private void saveFile(CommandSender sender, @CommandParameter(name = "fileName") FileUtil fileUtil) {
        fileUtil.save();
        sender.sendMessage("§7File §6" + fileUtil.getFileName() + " §7saved successfully.");
    }

    @SubCommand(subCommand = "reset")
    private void resetFile(CommandSender sender, @CommandParameter(name = "fileName") FileUtil fileUtil) {
        fileUtil.resetConfig();
        sender.sendMessage("§7File §6" + fileUtil.getFileName() + " §7reset successfully.");
    }
}
