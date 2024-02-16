package de.hits.prison.base.command.parser;

import de.hits.prison.base.command.anno.AdditionalParser;
import de.hits.prison.base.command.anno.DoubleParameter;
import de.hits.prison.base.command.helper.ArgumentParser;
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
        double number;

        try {
            number = Double.parseDouble(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid double format: §6" + arg);
        }

        checkRange(number, parameter);
        return number;
    }

    private void checkRange(double number, Parameter parameter) throws IllegalArgumentException {
        if (!parameter.isAnnotationPresent(DoubleParameter.class))
            return;
        DoubleParameter doubleParameter = parameter.getAnnotation(DoubleParameter.class);
        double min = doubleParameter.min();
        double max = doubleParameter.max();
        switch (doubleParameter.limit()) {
            case MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cDouble value exceeds max value of: §6" + max);
                break;
            case MIN:
                if (number < min)
                    throw new IllegalArgumentException("§cDouble value is less than min value of: §6" + min);
                break;
            case MIN_MAX:
                if (number > max)
                    throw new IllegalArgumentException("§cDouble value exceeds max value of: §6" + max);
                if (number < min)
                    throw new IllegalArgumentException("§cDouble value is less than min value of: §6" + min);
                break;
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

