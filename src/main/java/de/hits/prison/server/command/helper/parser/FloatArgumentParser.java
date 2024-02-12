package de.hits.prison.server.command.helper.parser;

import de.hits.prison.server.command.anno.AdditionalParser;
import de.hits.prison.server.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

@AdditionalParser(float.class)
public class FloatArgumentParser extends ArgumentParser<Float> {
    public FloatArgumentParser() {
        super(Float.class);
    }

    @Override
    public Float parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        try {
            return Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid float format: §6" + arg);
        }
    }

    @Override
    public String format(Float value) {
        return String.valueOf(value);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        return List.of(); // No tab completions for floats
    }
}
