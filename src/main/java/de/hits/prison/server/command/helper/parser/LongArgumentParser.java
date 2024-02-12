package de.hits.prison.server.command.helper.parser;

import de.hits.prison.server.command.anno.AdditionalParser;
import de.hits.prison.server.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

@AdditionalParser(long.class)
public class LongArgumentParser extends ArgumentParser<Long> {
    public LongArgumentParser() {
        super(Long.class);
    }

    @Override
    public Long parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        try {
            return Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid long format: §6" + arg);
        }
    }

    @Override
    public String format(Long value) {
        return String.valueOf(value);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        return List.of(); // No tab completions for longs
    }
}


