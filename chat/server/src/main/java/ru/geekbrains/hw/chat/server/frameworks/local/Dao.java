package ru.geekbrains.hw.chat.server.frameworks.local;

import java.sql.Connection;
import java.sql.SQLException;

abstract class Dao {

    private Connection connection;

    void setConnection(Connection connection) {
        this.connection = connection;
    }

    protected Connection getConnection() {
        if (connection == null) {
            throw new RuntimeException("No database connection");
        }
        return connection;
    }

    abstract void close() throws SQLException;
}
