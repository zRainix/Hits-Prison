package de.hits.prison.mechanic.server.command;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.command.anno.BaseCommand;
import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.anno.SubCommand;
import de.hits.prison.command.helper.AdvancedCommand;
import de.hits.prison.command.helper.SimpleCommand;
import de.hits.prison.fileUtil.helper.FileUtil;
import de.hits.prison.fileUtil.helper.FileUtilManager;
import org.bukkit.command.CommandSender;

public class FileUtilCommand extends AdvancedCommand {

    @Autowired
    private static FileUtilManager fileUtilManager;

    public FileUtilCommand(FileUtilManager fileUtilManager) {
        super("fileutil");
        this.fileUtilManager = fileUtilManager;
    }

    @SubCommand(subCommand = "load")
    private void loadFile(CommandSender sender, FileUtil fileUtil) {
        fileUtil = getFileUtil(fileUtil);
        if(fileUtil != null) {
            fileUtil.load();
            sender.sendMessage("File " + fileUtil + " loaded successfully");
        } else {
            sender.sendMessage("FileUtil not found: " + fileUtil);
        }

        sender.sendMessage();
    }

    @SubCommand(subCommand = "save")
    private void saveFile(CommandSender sender, FileUtil fileUtil) {
        fileUtil = getFileUtil(fileUtil);
        if(fileUtil != null) {
            fileUtil.save();
            sender.sendMessage("File " + fileUtil + " saved successfully");
        } else {
            sender.sendMessage("FileUtil not found: " + fileUtil);
        }

        sender.sendMessage();
    }

    @SubCommand(subCommand = "reset")
    private void resetFile(CommandSender sender, FileUtil fileUtil) {
        fileUtil = getFileUtil(fileUtil);
        if (fileUtil != null) {
            fileUtil.resetConfig();
            sender.sendMessage("File " + fileUtil + " reseted successfully");
        } else {
            sender.sendMessage("FileUtil not found: " + fileUtil);
        }

        sender.sendMessage();
    }

    private de.hits.prison.fileUtil.helper.FileUtil getFileUtil(FileUtil fileUtil) {
        return fileUtil;
    }
}
