package de.hits.prison.command.helper;

import de.hits.prison.command.anno.AdditionalParser;
import de.hits.prison.model.helper.ClassScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ArgumentParserRegistry {
    private static final Map<Class<?>, ArgumentParser<?>> parsers = new HashMap<>();

    public static void registerParser(Class<?> type, ArgumentParser<?> parser) {
        parsers.put(type, parser);
    }

    public static ArgumentParser<?> getParser(Class<?> type) {
        return parsers.get(type);
    }

    public static void registerAll(String packageName) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Set<Class<? extends ArgumentParser>> classes = ClassScanner.getClasses(packageName, ArgumentParser.class);

        classes = classes.stream().filter(clazz -> clazz.getSuperclass() == ArgumentParser.class).collect(Collectors.toSet());

        for (Class<?> clazz : classes) {
            Class<?> type = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
            registerParser(type, (ArgumentParser<?>) clazz.getConstructor().newInstance());

            if(type.isAnnotationPresent(AdditionalParser.class)) {
                for(Class additionalClass : type.getAnnotation(AdditionalParser.class).value()) {
                    registerParser(additionalClass, (ArgumentParser<?>) clazz.getConstructor().newInstance());
                }
            }
        }
    }
}

