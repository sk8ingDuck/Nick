package me.sk8ingduck.nick.config;

import java.io.File;

public class DBConfig extends Config {

    private final String databaseType;
    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;

    public DBConfig(String name, File path) {
        super(name, path);

        this.databaseType = (String) getPathOrSet("databaseType", "SQLite", false);

        this.host = (String) getPathOrSet("mysql.host", "localhost", false);
        this.port = (int) getPathOrSet("mysql.port", 3306, false);
        this.username = (String) getPathOrSet("mysql.username", "root", false);
        this.password = (String) getPathOrSet("mysql.password", "pw", false);
        this.database = (String) getPathOrSet("mysql.database", "db", false);

        comment = "########################################\n" +
                "#                                       #\n" +
                "#            Database Settings          #\n" +
                "#                                       #\n" +
                "########################################\n"
                + "\n" +
                "# This section defines the database configuration for the plugin.\n" +
                "#\n" +
                "# databaseType: The type of database to use.\n" +
                "#               'MySQL' - To use a MySQL database.\n" +
                "#               'SQLite' - To use a SQLite (Flatfile / local) database.\n" +
                "#\n" +
                "# If you use SQLite you don't need to enter any information. \n" +
                "# If you use MySQL make sure to enter correct information for your MySQL database.\n";
        saveComment();
    }

    public boolean isMySQLEnabled() {
        return databaseType.equalsIgnoreCase("mysql");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }
}
