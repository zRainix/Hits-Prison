package de.hits.prison.base.autowire.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.helper.ClassScanner;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;


public class AutowiredManager {

    private static Map<String, List<Field>> autowiredFields = null;

    private static void init() {
        autowiredFields = new HashMap<>();

        Set<Class<?>> components = ClassScanner.getClassesByAnnotation(HitsPrison.class.getPackageName(), Component.class);

        for (Class<?> component : components) {
            for (Field field : component.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class) && Modifier.isStatic(field.getModifiers())) {
                    Class<?> fieldType = field.getType();
                    String key = fieldType.getName();
                    List<Field> fields = autowiredFields.getOrDefault(key, new ArrayList<>());
                    fields.add(field);
                    if (autowiredFields.containsKey(key)) {
                        autowiredFields.replace(key, fields);
                    } else {
                        autowiredFields.put(key, fields);
                    }
                }
            }
        }
    }

    public static <T>  void register(T entity) {
        register(entity, entity.getClass());
    }

    public static <T> void register(T entity, Class<? extends T> type) {
        if (autowiredFields == null) {
            init();
        }

        List<Field> fields = autowiredFields.getOrDefault(type.getName(), new ArrayList<>());
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                field.set(null, entity);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Could not autowire " + entity.getClass().getSimpleName() + " for " + field + ".");
            }
        }
    }

}
