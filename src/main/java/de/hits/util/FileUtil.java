package de.hits.util;

import java.io.*;
import java.util.Properties;

public class FileUtil {

    private String fileName;
    private Properties properties;

    public FileUtil(String fileName) {
        this.fileName = fileName;
        this.properties = new Properties();
    }

    public void createSettings(String key, String value) {
        properties.setProperty(key, value);
    }

    public void saveSettings() {
        try(OutputStream output = new FileOutputStream(fileName)) {
            properties.store(output, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSettings() {
        try(InputStream input = new FileInputStream(fileName)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getKey(String key) {
        return properties.getProperty(key);
    }
}
