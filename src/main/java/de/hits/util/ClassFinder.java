package de.hits.util;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassFinder {

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

    public List<ClassPath.ClassInfo> findClasses() {
        Set<ClassPath.ClassInfo> classes = this.classpath.getTopLevelClassesRecursive(this.packageName);
        return classes.stream().filter(info -> filterAnnotations(info) && filterTypes(info)).collect(Collectors.toList());
    }

    private boolean filterAnnotations(ClassPath.ClassInfo classInfo) {
        for (Class<? extends Annotation> annotation : annotations) {
            if(!classInfo.load().isAnnotationPresent(annotation)) {
                return false;
            }
        }
        return true;
    }

    private boolean filterTypes(ClassPath.ClassInfo classInfo) {
        for (Class<?> type : types) {
            if(!classInfo.load().isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
}
