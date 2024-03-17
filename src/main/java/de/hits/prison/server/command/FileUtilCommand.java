package de.hits.prison.server.command;

import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.command.anno.CommandParameter;
import de.hits.prison.base.command.anno.SubCommand;
import de.hits.prison.base.command.helper.AdvancedCommand;
import de.hits.prison.base.fileUtil.helper.FileUtil;
import de.hits.prison.server.util.MessageUtil;
import org.bukkit.command.CommandSender;

@Component
public class FileUtilCommand extends AdvancedCommand {

    public FileUtilCommand() {
        super("fileUtil");
    }

    @SubCommand("load")
    private void loadFile(CommandSender sender, @CommandParameter(name = "fileName") FileUtil fileUtil) {
        fileUtil.load();
        MessageUtil.sendMessage(sender, "§7File §6" + fileUtil.getFileName() + " §7loaded successfully.");
    }

    @SubCommand("save")
    private void saveFile(CommandSender sender, @CommandParameter(name = "fileName") FileUtil fileUtil) {
        fileUtil.save();
        MessageUtil.sendMessage(sender, "§7File §6" + fileUtil.getFileName() + " §7saved successfully.");
    }

    @SubCommand("reset")
    private void resetFile(CommandSender sender, @CommandParameter(name = "fileName") FileUtil fileUtil) {
        fileUtil.resetConfig();
        MessageUtil.sendMessage(sender, "§7File §6" + fileUtil.getFileName() + " §7reset successfully.");
    }
}
