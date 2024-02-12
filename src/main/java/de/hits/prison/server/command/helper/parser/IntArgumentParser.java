package de.hits.prison.server.command.helper.parser;

import de.hits.prison.server.command.anno.AdditionalParser;
import de.hits.prison.server.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

@AdditionalParser(int.class)
public class IntArgumentParser extends ArgumentParser<Integer> {
    public IntArgumentParser() {
        super(Integer.class);
    }

    @Override
    public Integer parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid integer format: §6" + arg);
        }
    }

    @Override
    public String format(Integer value) {
        return String.valueOf(value);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        return List.of(); // No tab completions for ints
    }
}
