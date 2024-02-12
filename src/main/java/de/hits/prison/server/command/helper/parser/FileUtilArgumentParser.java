package de.hits.prison.server.command.helper.parser;

import de.hits.prison.server.autowire.anno.Autowired;
import de.hits.prison.server.autowire.anno.Component;
import de.hits.prison.server.command.helper.ArgumentParser;
import de.hits.prison.server.fileUtil.helper.FileUtil;
import de.hits.prison.server.fileUtil.helper.FileUtilManager;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileUtilArgumentParser extends ArgumentParser<FileUtil> {

    @Autowired
    private static FileUtilManager fileUtilManager;

    public FileUtilArgumentParser() {
        super(FileUtil.class);
    }

    @Override
    public FileUtil parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        FileUtil fileUtil = fileUtilManager.getFileUtilByName(arg);
        if (fileUtil == null) {
            throw new IllegalArgumentException("§cFileUtil not found: §6" + arg);
        }
        return fileUtil;
    }

    @Override
    public String format(FileUtil fileValue) {
        return String.valueOf(fileValue.getFileName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        List<String> completions = new ArrayList<>();
        for (FileUtil fileUtil : fileUtilManager.getAllFileUtils()) {
            if (fileUtil.getFileName().toLowerCase().startsWith(arg.toLowerCase())) {
                completions.add(fileUtil.getFileName());
            }
        }
        return completions;
    }
}