package de.hits.prison.model.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.autowire.anno.Autowired;
import de.hits.prison.autowire.anno.Component;
import de.hits.prison.autowire.helper.AutowiredManager;
import de.hits.prison.fileUtil.anno.SettingsFile;
import de.hits.prison.mechanic.server.fileUtil.SettingsUtil;
import de.hits.prison.model.anno.Repository;
import de.hits.prison.scheduler.anno.Scheduler;
import de.hits.prison.scheduler.helper.CustomScheduler;
import org.bukkit.Bukkit;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class HibernateUtil {

    static Logger logger = Bukkit.getLogger();

    private static SessionFactory sessionFactory;

    @Autowired
    private static SettingsUtil settingsUtil;

    public static void init(HitsPrison main) {
        if (sessionFactory == null) {
            logger.info("Initializing hibernate...");

            try {
                logger.info("Setting up hibernate environment variables...");
                Map<String, String> settings = new HashMap<>();
                settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://" + settingsUtil.getHost() + ":" + settingsUtil.getPort() + "/" + settingsUtil.getDatabase() + "");
                settings.put(Environment.USER, settingsUtil.getUser());
                settings.put(Environment.PASS, settingsUtil.getPassword());
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.PHYSICAL_NAMING_STRATEGY, "de.hits.prison.model.helper.CustomPhysicalNamingStrategy");
                settings.put(Environment.POOL_SIZE, "5");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.SHOW_SQL, "false");

                logger.info("Creating registryBuilder...");
                StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
                registryBuilder.applySettings(settings);

                logger.info("Building registry...");
                StandardServiceRegistry registry = registryBuilder.build();

                logger.info("Creating metadata...");
                MetadataSources sources = new MetadataSources(registry);

                logger.info("Finding all entity classes.");
                Set<Class<?>> classes = ClassScanner.getClasses("de.hits.prison.model.entity");
                for (Class<?> clazz : classes) {
                    sources.addAnnotatedClass(clazz);
                    logger.info("- Class: " + clazz.getSimpleName() + " registered.");
                }

                logger.info("Building metadata...");
                Metadata metadata = sources.buildMetadata();

                logger.info("Building sessionFactory...");
                sessionFactory = metadata.buildSessionFactory();

                logger.info("Hibernate initialized.");
            } catch (Exception e) {
                logger.severe("An error occurred when initializing hibernate: " + e.getMessage());
                e.printStackTrace();
                if (sessionFactory != null) {
                    sessionFactory.close();
                }
            }
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            logger.info("Shutting down hibernate...");
            sessionFactory.close();
            logger.info("Hibernate shut down.");
        }
    }

    public static void registerAllRepositories(String packageName) {
        Set<Class<?>> repositories = ClassScanner.getClassesByAnnotation(packageName, Repository.class);
        try {
            for (Class<?> repository : repositories) {
                Repository schedulerAnno = repository.getAnnotation(Repository.class);

                if (repository.getSuperclass() == PrisonRepository.class) {
                    PrisonRepository prisonRepository = (PrisonRepository) repository.getConstructor().newInstance();
                    AutowiredManager.register(prisonRepository);
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.severe("Error while initializing managers: " + e.getMessage());
        }
    }
}
