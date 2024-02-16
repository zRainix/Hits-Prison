package de.hits.prison.base.command.parser;

import de.hits.prison.base.command.anno.BigIntegerParameter;
import de.hits.prison.base.command.helper.ArgumentParser;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.List;

public class BigIntegerArgumentParser extends ArgumentParser<BigInteger> {
    public BigIntegerArgumentParser() {
        super(BigInteger.class);
    }

    @Override
    public BigInteger parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException {
        BigInteger number;

        try {
            number = new BigInteger(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid big integer format: §6" + arg);
        }

        checkRange(number, parameter);
        return number;
    }

    private void checkRange(BigInteger number, Parameter parameter) throws IllegalArgumentException {
        if (!parameter.isAnnotationPresent(BigIntegerParameter.class))
            return;
        BigIntegerParameter bigIntegerParameter = parameter.getAnnotation(BigIntegerParameter.class);
        BigInteger min = new BigInteger(bigIntegerParameter.min());
        BigInteger max = new BigInteger(bigIntegerParameter.max());
        switch (bigIntegerParameter.limit()) {
            case MAX:
                if (number.compareTo(max) > 0)
                    throw new IllegalArgumentException("§cBigInteger value exceeds max value of: §6" + max);
                break;
            case MIN:
                if (number.compareTo(min) < 0)
                    throw new IllegalArgumentException("§cBigInteger value is less than min value of: §6" + min);
                break;
            case MIN_MAX:
                if (number.compareTo(max) > 0)
                    throw new IllegalArgumentException("§cBigInteger value exceeds max value of: §6" + max);
                if (number.compareTo(min) < 0)
                    throw new IllegalArgumentException("§cBigInteger value is less than min value of: §6" + min);
                break;
        }
    }

    @Override
    public String format(BigInteger value) {
        return value.toString();
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String arg, Parameter parameter) {
        return List.of(); // No tab completions for ints
    }
}
