package de.hits.prison.base.command.parser;

import de.hits.prison.base.command.anno.AdditionalParser;
import de.hits.prison.base.command.anno.LongParameter;
import de.hits.prison.base.command.helper.ArgumentParser;
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
        long number;

        try {
            number = Long.parseLong(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid long format: §6" + arg);
        }

        checkRange(number, parameter);
        return number;
    }

    private void checkRange(long number, Parameter parameter) throws IllegalArgumentException {
        if (!parameter.isAnnotationPresent(LongParameter.class))
            return;
        LongParameter longParameter = parameter.getAnnotation(LongParameter.class);
        long min = longParameter.min();
        long max = longParameter.max();
        switch (longParameter.limit()) {
            case MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cLong value exceeds max value of: §6" + max);
                break;
            case MIN:
                if (number < min)
                    throw new IllegalArgumentException("§cLong value is less than min value of: §6" + min);
                break;
            case MIN_MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cLong value exceeds max value of: §6" + max);
                if (number < min)
                    throw new IllegalArgumentException("§cLong value is less than min value of: §6" + min);
                break;
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


