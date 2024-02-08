package de.hits.prison.model.helper;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.Set;

public class ClassScanner {

    public static Set<Class<?>> getClasses(String packageName) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName)))
                .setScanners(new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getSubTypesOf(Object.class);
    }

    public static Set<Class<?>> getClasses(String packageName, Class clazz) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName)))
                .setScanners(new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getSubTypesOf(clazz);
    }

    public static Set<Class<?>> getClassesByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName)))
                .setScanners(new SubTypesScanner(false))
                .setUrls(ClasspathHelper.forPackage(packageName)));

        return reflections.getTypesAnnotatedWith(annotation);
    }
}

