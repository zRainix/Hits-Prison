package de.hits.prison.base.command.parser;

import de.hits.prison.base.command.anno.AdditionalParser;
import de.hits.prison.base.command.anno.IntParameter;
import de.hits.prison.base.command.helper.ArgumentParser;
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
        int number;

        try {
            number = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid integer format: §6" + arg);
        }

        checkRange(number, parameter);

        return number;
    }

    private void checkRange(int number, Parameter parameter) throws IllegalArgumentException {
        if (!parameter.isAnnotationPresent(IntParameter.class))
            return;
        IntParameter intParameter = parameter.getAnnotation(IntParameter.class);
        int min = intParameter.min();
        int max = intParameter.max();
        switch (intParameter.limit()) {
            case MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cInteger value exceeds max value of: §6" + max);
                break;
            case MIN:
                if (number < min)
                    throw new IllegalArgumentException("§cInteger value is less than min value of: §6" + min);
                break;
            case MIN_MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cInteger value exceeds max value of: §6" + max);
                if (number < min)
                    throw new IllegalArgumentException("§cInteger value is less than min value of: §6" + min);
            break;
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
