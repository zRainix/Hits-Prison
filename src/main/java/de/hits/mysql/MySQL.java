package de.hits.mysql;

import de.hits.util.impl.SettingsUtil;

import java.sql.*;

public class MySQL {

    private Connection connection;
    private final SettingsUtil settingsUtil;

    public MySQL(SettingsUtil settingsUtil) {
        this.settingsUtil = settingsUtil;
    }

    public void connect() {
        try {
            String url = "jdbc:mysql://" + this.settingsUtil.getHost() + ":" + this.settingsUtil.getPort() + "/" + this.settingsUtil.getDatabase();
            this.connection = DriverManager.getConnection(url, this.settingsUtil.getUser(), this.settingsUtil.getPassword());
            System.out.println("Connected to the database");

            createTable();
        } catch (SQLException error) {
            System.out.println("Error connecting to the database: " + error.getMessage());
        }
    }

    public void disconnect() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("Disconnected from the database");
            }
        } catch (SQLException error) {
            System.out.println("Error disconnecting from the database: " + error.getMessage());
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                Statement statement = this.connection.createStatement();
                return statement.executeQuery(query);
            }
        } catch (SQLException error) {
            System.out.println("Error executing query: " + error.getMessage());
        }
        return null;
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS weloverainix ("
                + "id INT PRIMARY KEY,"
                + "name VARCHAR(255),"
                + "age INT"
                + ");";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Table created successfully");
        } catch (SQLException error) {
            System.out.println("Error creating table: " + error.getMessage());
        }
    }

}
