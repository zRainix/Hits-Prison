package de.hits.prison.base.model.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.server.fileUtil.SettingsUtil;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.MySQL8Dialect;

import javax.persistence.Entity;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

@Component
public class HibernateUtil {

    @Autowired
    private static Logger logger;

    private static SessionFactory sessionFactory;

    @Autowired
    private static SettingsUtil settingsUtil;
    @Autowired
    private static HitsPrison main;

    public static void init(HitsPrison main) {
        if (sessionFactory == null) {
            logger.info("Initializing hibernate...");

            try {
                logger.info("Setting up hibernate environment variables...");
                Map<String, String> settings = new HashMap<>();

                if (settingsUtil.isRemoteMySQL()) {
                    logger.info("Remote MySQL enabled. Connecting to remote host.");

                    settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
                    settings.put(Environment.URL, "jdbc:mysql://" + settingsUtil.getHost() + ":" + settingsUtil.getPort() + "/" + settingsUtil.getDatabase() + "?createDatabaseIfNotExist=true");
                    settings.put(Environment.USER, settingsUtil.getUser());
                    settings.put(Environment.PASS, settingsUtil.getPassword());
                } else {
                    logger.info("Remote MySQL not enabled. Starting local database.");

                    File databaseFolder = new File(main.getDataFolder(), "localDatabase");
                    databaseFolder.mkdirs();

                    File databaseFile = new File(databaseFolder, "local");

                    settings.put(Environment.DRIVER, "org.h2.Driver");
                    settings.put(Environment.URL, "jdbc:h2:" + databaseFile.getAbsolutePath() + ";MODE=MySQL");
                    settings.put(Environment.USER, "sa");
                    settings.put(Environment.PASS, "");
                }

                settings.put(Environment.DIALECT, MySQL8Dialect.class.getName());
                settings.put(Environment.HBM2DDL_AUTO, "update");
                settings.put(Environment.PHYSICAL_NAMING_STRATEGY, CustomPhysicalNamingStrategy.class.getName());
                settings.put(Environment.POOL_SIZE, "10");
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
                Set<Class<?>> classes = ClassScanner.getClassesByAnnotation(HitsPrison.class.getPackageName(), Entity.class);
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
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            logger.severe("Error while initializing repositories: " + e.getMessage());
        }
    }
}
