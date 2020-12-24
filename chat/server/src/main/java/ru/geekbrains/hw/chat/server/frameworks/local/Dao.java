package ru.geekbrains.hw.chat.server.frameworks.local;

import java.sql.Connection;
import java.sql.SQLException;

abstract class Dao {

    private final LocalDataSource dataSource;

    public Dao(LocalDataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() {
        return dataSource.getConnection();
    }

    abstract void close() throws SQLException;
}
