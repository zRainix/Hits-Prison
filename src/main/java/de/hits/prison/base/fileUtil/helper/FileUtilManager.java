package de.hits.prison.base.fileUtil.helper;

import de.hits.prison.base.autowire.helper.AutowiredManager;
import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.model.helper.ClassScanner;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class FileUtilManager {

    private Logger logger = Bukkit.getLogger();

    private List<FileUtil> registeredFileUtils;

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

                if (settingsUtil.getSuperclass() == FileUtil.class) {
                    FileUtil fileUtil = (FileUtil) settingsUtil.getConstructor().newInstance();
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
            logger.severe("Error while initializing file utils: " + e.getMessage());
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
