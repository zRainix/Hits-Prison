package de.hits.util.impl;

import de.hits.util.FileUtil;

public class SettingsUtil extends FileUtil {

    // Datenfelder

    public SettingsUtil() {
        super("settings.yaml");
    }

    @Override
    public void init() {
        // TODO: Standartwerte initialisieren
    }

    @Override
    public void save() {
        // TODO
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();
        // TODO
    }
}
