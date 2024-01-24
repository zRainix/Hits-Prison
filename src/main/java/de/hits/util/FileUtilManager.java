package de.hits.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtilManager {

    private FileUtil fileUtil;
    private String fileName;

    public FileUtilManager(String fileName) {
        this.fileName = fileName;
        this.fileUtil = new FileUtil(fileName);
    }

    public void start() {
        File file = new File(fileName);

        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                System.out.println("created folder " + file.getName());
                fileUtil.createSettings("key1", "batch1");
                fileUtil.createSettings("key2", "batch2");
                fileUtil.createSettings("key3", "batch3");
                fileUtil.saveSettings();
            } else {
                System.out.println("failed to create folder - file already exists");
                fileUtil.loadSettings();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        fileUtil.saveSettings();
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }
}
