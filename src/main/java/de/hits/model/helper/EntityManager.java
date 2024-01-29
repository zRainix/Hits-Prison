package de.hits.model.helper;

import com.google.common.reflect.ClassPath;
import de.hits.model.helper.anno.Entity;
import de.hits.model.helper.anno.Id;
import de.hits.util.AutowiredManager;
import de.hits.util.ClassFinder;

import java.io.IOException;
import java.util.Set;

public class EntityManager {

    private AutowiredManager autowiredManager;

    public EntityManager(AutowiredManager autowiredManager) throws IOException {
        this.autowiredManager = autowiredManager;
    }

    public void registerEntities(String packageName) throws IOException {
        for(Class<?> clazz : new ClassFinder(packageName).setAnnotationFilter(Entity.class).find()) {
            System.out.println(clazz.getName());
            System.out.println("- table: " + clazz.getAnnotation(Entity.class).table());
        }
    }

}
