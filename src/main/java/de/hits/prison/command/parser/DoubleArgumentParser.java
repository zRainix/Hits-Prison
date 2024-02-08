package de.hits.prison.command.parser;

import de.hits.prison.command.anno.AdditionalParser;
import de.hits.prison.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

@AdditionalParser(double.class)
public class DoubleArgumentParser extends ArgumentParser<Double> {
    public DoubleArgumentParser() {
        super(Double.class);
    }

    @Override
    public Double parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        try {
            return Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid double format: §6" + arg);
        }
    }

    @Override
    public String format(Double value) {
        return String.valueOf(value);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        return List.of(); // No tab completions for doubles
    }
}

