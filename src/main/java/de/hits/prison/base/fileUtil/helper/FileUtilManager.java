package de.hits.prison.base.fileUtil.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.model.helper.ClassScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FileUtilManager {

    @Autowired
    private static Logger logger;

    private final List<FileUtil> registeredFileUtils;

    public FileUtilManager() {
        this.registeredFileUtils = new ArrayList<>();
    }

    public void registerFileUtil(FileUtil fileUtil) {
        if (!this.registeredFileUtils.contains(fileUtil)) {
            this.registeredFileUtils.add(fileUtil);
        }
    }

    public void unregisterFileUtil(FileUtil fileUtil) {
        if (this.registeredFileUtils.contains(fileUtil)) {
            this.registeredFileUtils.remove(fileUtil);
        }
    }

    public void initAll() {
        for (FileUtil fileUtil : this.registeredFileUtils) {
            fileUtil.createFileIfNotExists();
            fileUtil.init();
        }
    }

    public void loadAll() {
        for (FileUtil fileUtil : this.registeredFileUtils) {
            fileUtil.load();
        }
    }

    public void saveAll() {
        for (FileUtil fileutil : this.registeredFileUtils) {
            fileutil.save();
        }
    }

    public void registerAllFileUtils(String packageName) {
        Set<Class<?>> settingsUtils = ClassScanner.getClassesByAnnotation(packageName, SettingsFile.class);
        try {
            for (Class<?> settingsUtil : settingsUtils) {
                SettingsFile settingsFile = settingsUtil.getAnnotation(SettingsFile.class);

                if (FileUtil.class.isAssignableFrom(settingsUtil.getSuperclass())) {

                    System.out.println(settingsUtil);
                    System.out.println(settingsFile);

                    Constructor<?> constructor = settingsUtil.getConstructor();
                    constructor.setAccessible(true);

                    FileUtil fileUtil = (FileUtil) constructor.newInstance();
                    registerFileUtil(fileUtil);

                    if (settingsFile.autoInit()) {
                        fileUtil.init();
                    }
                    if (settingsFile.autoLoad()) {
                        fileUtil.load();
                    }

                    AutowiredManager.register(fileUtil);
                }
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            logger.log(Level.SEVERE,"Error while initializing file utils: " + e.getMessage(), e);
        }
    }

    public FileUtil getFileUtilByName(String fileName) {
        for (FileUtil fileUtil : registeredFileUtils) {
            if (fileUtil.getFileName().equalsIgnoreCase(fileName)) {
                return fileUtil;
            }
        }
        return null;
    }

    public List<FileUtil> getAllFileUtils() {
        return registeredFileUtils;
    }

}
