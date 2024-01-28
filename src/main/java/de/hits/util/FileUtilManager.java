package de.hits.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtilManager {

    private List<FileUtil> registeredFileUtils;

    public FileUtilManager() {
        this.registeredFileUtils = new ArrayList<>();
    }

    public void registerFileUtil(FileUtil fileUtil) {
        if(!registeredFileUtils.contains(fileUtil)) {
            registeredFileUtils.add(fileUtil);
        }
    }

    public void unregisterFileUtil(FileUtil fileUtil) {
        if(registeredFileUtils.contains(fileUtil)) {
            registeredFileUtils.remove(fileUtil);
        }
    }

    public void initAll() {
        for(FileUtil fileUtil : registeredFileUtils) {
            fileUtil.createFileIfNotExists();
            fileUtil.init();
        }
    }

    public void loadAll() {
        for(FileUtil fileUtil : registeredFileUtils) {
            fileUtil.load();
        }
    }

    public void saveAll() {
        for(FileUtil fileutil : registeredFileUtils) {
            fileutil.save();
        }
    }
}
