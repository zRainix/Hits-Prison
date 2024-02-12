package de.hits.prison.command.parser;

import de.hits.prison.command.helper.ArgumentParser;
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
        try {
            return new BigInteger(arg);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("§cInvalid integer format: §6" + arg);
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
