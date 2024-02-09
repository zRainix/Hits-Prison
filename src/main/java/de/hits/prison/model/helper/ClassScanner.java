package de.hits.prison.model.helper;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassScanner {

    public static Set<Class<?>> getClasses(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getSubTypesOf(Object.class).stream().filter(clazz -> clazz.getPackageName().startsWith(packageName)).collect(Collectors.toSet());
    }

    public static <T> Set<Class<? extends T>> getClasses(String packageName, Class<T> clazz) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getSubTypesOf(clazz).stream().filter(c -> c.getPackageName().startsWith(packageName)).collect(Collectors.toSet());
    }

    public static Set<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getTypesAnnotatedWith(annotation).stream().filter(clazz -> clazz.getPackageName().startsWith(packageName)).collect(Collectors.toSet());
    }
}

