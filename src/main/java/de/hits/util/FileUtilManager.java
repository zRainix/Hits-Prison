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
        // TODO
    }

    public void loadAll() {
        // TODO
    }

    public void saveAll() {
        // TODO
    }
}
