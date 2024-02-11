package de.hits.prison.mechanic.server.command;

import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.command.anno.BaseCommand;
import de.hits.prison.command.anno.CommandParameter;
import de.hits.prison.command.helper.SimpleCommand;
import de.hits.prison.fileUtil.helper.FileUtil;
import de.hits.prison.fileUtil.helper.FileUtilManager;
import org.bukkit.command.CommandSender;

public class FileUtilCommand extends SimpleCommand {

    @Autowired
    private static FileUtilManager fileUtilManager;

    public FileUtilCommand(FileUtilManager fileUtilManager) {
        super("fileutil");
        this.fileUtilManager = fileUtilManager;
    }

    @BaseCommand
    public void fileUtilCommand(CommandSender sender,
                                @CommandParameter(name = "action") String action,
                                @CommandParameter(name = "fileName") String fileName) {

        switch(action.toLowerCase()) {
            case "load":
                loadFile(sender, fileName);
                break;
            case "save":
                saveFile(sender , fileName);
                break;
            case "reset":
                resetFile(sender ,fileName);
                break;
            default:
                sender.sendMessage("Invalid subCommand, refer to <load|save|reset>");
                break;
        }
    }

    private void loadFile(CommandSender sender, String fileName) {
        //TODO
        FileUtil fileUtil = getFileUtil(fileName);
        if(fileUtil != null) {
            fileUtil.load();
            sender.sendMessage("File " + fileName + " loaded successfully");
        } else {
            sender.sendMessage("FileUtil not found: " + fileName);
        }

        sender.sendMessage();
    }

    private void saveFile(CommandSender sender, String fileName) {
        //TODO
        FileUtil fileUtil = getFileUtil(fileName);
        if(fileUtil != null) {
            fileUtil.save();
            sender.sendMessage("File " + fileName + " saved successfully");
        } else {
            sender.sendMessage("FileUtil not found: " + fileName);
        }

        sender.sendMessage();
    }

    private void resetFile(CommandSender sender, String fileName) {
        //TODO
        FileUtil fileUtil = getFileUtil(fileName);
        if (fileUtil != null) {
            fileUtil.resetConfig();
            sender.sendMessage("File " + fileName + " reseted successfully");
        } else {
            sender.sendMessage("FileUtil not found: " + fileName);
        }

        sender.sendMessage();
    }

    private de.hits.prison.fileUtil.helper.FileUtil getFileUtil(String fileName) {
        return fileUtilManager.getFileUtilByName(fileName);
    }
}
