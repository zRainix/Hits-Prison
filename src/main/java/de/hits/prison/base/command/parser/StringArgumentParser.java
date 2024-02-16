package de.hits.prison.base.command.parser;

import de.hits.prison.base.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

public class StringArgumentParser extends ArgumentParser<String> {
    public StringArgumentParser() {
        super(String.class);
    }

    @Override
    public String parse(CommandSender sender, String arg, Parameter parameter) {
        return arg;
    }

    @Override
    public String format(String value) {
        return value;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        return List.of(); // No tab completions for strings
    }
}

