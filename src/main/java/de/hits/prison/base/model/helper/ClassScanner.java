package de.hits.prison.base.model.helper;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassScanner {

    public static String getPackageNameOfParallelPackage(String packageName, String parallelPackage) {
        if (!packageName.contains("."))
            return packageName;
        String[] split = packageName.split("\\.");
        return String.join(".", Arrays.copyOf(split, split.length - 1)) + "." + parallelPackage;
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getSubTypesOf(Object.class).stream().filter(clazz -> clazz.getPackageName().startsWith(packageName)).collect(Collectors.toSet());
    }

    public static <T> Set<Class<? extends T>> getClassesBySuperclass(String packageName, Class<T> clazz) {
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

