package de.hits.prison.base.command.parser;

import de.hits.prison.base.command.anno.AdditionalParser;
import de.hits.prison.base.command.anno.FloatParameter;
import de.hits.prison.base.command.helper.ArgumentParser;
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
        float number;

        try {
            number = Float.parseFloat(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid float format: §6" + arg);
        }

        checkRange(number, parameter);
        return number;
    }

    private void checkRange(float number, Parameter parameter) throws IllegalArgumentException {
        if (!parameter.isAnnotationPresent(FloatParameter.class))
            return;
        FloatParameter floatParameter = parameter.getAnnotation(FloatParameter.class);
        float min = floatParameter.min();
        float max = floatParameter.max();
        switch (floatParameter.limit()) {
            case MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cFloat value exceeds max value of: §6" + max);
                break;
            case MIN:
                if (number < min)
                    throw new IllegalArgumentException("§cFloat value is less than min value of: §6" + min);
                break;
            case MIN_MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cFloat value exceeds max value of: §6" + max);
                if (number < min)
                    throw new IllegalArgumentException("§cFloat value is less than min value of: §6" + min);
                break;
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
