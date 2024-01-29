package de.hits.util;

import com.google.common.reflect.ClassPath;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassFinder {

    private final char PKG_SEPARATOR = '.';
    private final char DIR_SEPARATOR = '/';
    private final String CLASS_FILE_SUFFIX = ".class";
    private final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";


    private ClassPath classpath;
    private String packageName;
    private Class<? extends Annotation>[] annotations;
    private Class<?>[] types;

    public ClassFinder(String packageName) throws IOException {
        this.classpath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        this.packageName = packageName;
        this.annotations = new Class[0];
        this.types = new Class[0];
    }
    public ClassFinder setAnnotationFilter(Class<? extends Annotation>... annotations) {
        this.annotations = annotations;
        return this;
    }

    public ClassFinder setTypeFilter(Class<?>... types) {
        this.types = types;
        return this;
    }

    public List<Class<?>> find() {
        String scannedPath = packageName.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, packageName));
        }
        File scannedDir = new File(scannedUrl.getFile());
        List<Class<?>> classes = new ArrayList<>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, packageName));
        }
        return classes.stream().filter(clazz -> filterAnnotations(clazz) && filterTypes(clazz)).collect(Collectors.toList());
    }

    private List<Class<?>> find(File file, String scannedPackage) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(CLASS_FILE_SUFFIX)) {
            int endIndex = resource.length() - CLASS_FILE_SUFFIX.length();
            String className = resource.substring(0, endIndex);
            try {
                classes.add(Class.forName(className));
            } catch (ClassNotFoundException ignore) {
            }
        }
        return classes;
    }

    private boolean filterAnnotations(Class classInfo) {
        for (Class<? extends Annotation> annotation : annotations) {
            if(!classInfo.isAnnotationPresent(annotation)) {
                return false;
            }
        }
        return true;
    }

    private boolean filterTypes(Class classInfo) {
        for (Class<?> type : types) {
            if(!classInfo.isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
}
