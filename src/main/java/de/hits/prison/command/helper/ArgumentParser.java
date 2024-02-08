package de.hits.prison.command.helper;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Parameter;
import java.util.List;

public abstract class ArgumentParser<T> {
    private final Class<T> type;

    public ArgumentParser(Class<T> type) {
        this.type = type;
    }

    public abstract T parse(CommandSender sender, String arg, Parameter parameter) throws IllegalArgumentException;

    public abstract String format(T value);

    public Class<T> getType() {
        return type;
    }

    public abstract List<String> tabComplete(CommandSender sender, String arg, Parameter parameter);
}