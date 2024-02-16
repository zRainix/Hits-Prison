package de.hits.prison.base.command.helper;

import de.hits.prison.base.command.anno.AdditionalParser;
import de.hits.prison.base.model.helper.ClassScanner;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ArgumentParserRegistry {

    private static final Logger logger = Bukkit.getLogger();

    private static final Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();

    public static void registerParser(Class<?> type, ArgumentParser<?> parser) {
        parsers.put(type, parser);
        logger.info("Parser " + type.getSimpleName() + " registered.");
    }

    public static ArgumentParser<?> getParser(Class<?> type) {
        return parsers.get(type);
    }

    public static void registerAll() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String packageName = ClassScanner.getPackageNameOfParallelPackage(ArgumentParserRegistry.class.getPackageName(), "parser");

        Set<Class<? extends ArgumentParser>> classes = ClassScanner.getClassesBySuperclass(packageName, ArgumentParser.class);

        classes = classes.stream().filter(clazz -> clazz.getSuperclass() == ArgumentParser.class).collect(Collectors.toSet());

        for (Class<?> clazz : classes) {
            Class<?> type = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
            registerParser(type, (ArgumentParser<?>) clazz.getConstructor().newInstance());

            if (clazz.isAnnotationPresent(AdditionalParser.class)) {
                for (Class<?> additionalClass : clazz.getAnnotation(AdditionalParser.class).value()) {
                    registerParser(additionalClass, (ArgumentParser<?>) clazz.getConstructor().newInstance());
                }
            }
        }
    }
}

