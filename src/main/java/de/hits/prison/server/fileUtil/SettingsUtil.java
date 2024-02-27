package de.hits.prison.server.fileUtil;

import de.hits.prison.base.fileUtil.anno.SettingsFile;
import de.hits.prison.base.fileUtil.helper.FileUtil;

@SettingsFile
public class SettingsUtil extends FileUtil {

    private String prefix;
    private boolean remoteMySQL;
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public SettingsUtil() {
        super("settings.yml");

        this.prefix = "§8[§b§lPRISON§8] §7";
        this.remoteMySQL = false;
        this.host = "localhost";
        this.port = 3306;
        this.database = "schema";
        this.user = "user";
        this.password = "password";
    }

    @Override
    public void init() {
        this.cfg.addDefault("prefix", this.prefix);
        this.cfg.addDefault("remoteMySQL", this.remoteMySQL);
        this.cfg.addDefault("host", this.host);
        this.cfg.addDefault("port", this.port);
        this.cfg.addDefault("database", this.database);
        this.cfg.addDefault("user", this.user);
        this.cfg.addDefault("password", this.password);
        saveDefaultsConfig();
    }

    @Override
    public void save() {
        this.cfg.set("prefix", this.prefix);
        this.cfg.set("remoteMySQL", this.remoteMySQL);
        this.cfg.set("host", this.host);
        this.cfg.set("port", this.port);
        this.cfg.set("database", this.database);
        this.cfg.set("user", this.user);
        this.cfg.set("password", this.password);
        saveConfig();
    }

    @Override
    public void load() {
        loadConfig();

        this.prefix = this.cfg.getString("prefix");
        this.remoteMySQL = this.cfg.getBoolean("remoteMySQL");
        this.host = this.cfg.getString("host");
        this.port = this.cfg.getInt("port");
        this.database = this.cfg.getString("database");
        this.user = this.cfg.getString("user");
        this.password = this.cfg.getString("password");
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isRemoteMySQL() {
        return remoteMySQL;
    }

    public void setRemoteMySQL(boolean remoteMySQL) {
        this.remoteMySQL = remoteMySQL;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHost() {
        return host;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return database;
    }
}
