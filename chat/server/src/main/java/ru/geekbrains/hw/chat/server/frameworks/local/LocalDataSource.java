package ru.geekbrains.hw.chat.server.frameworks.local;

import ru.geekbrains.hw.chat.server.adapters.data.DataSource;
import ru.geekbrains.hw.chat.server.entities.User;

import java.sql.*;

public class LocalDataSource implements DataSource {

    private Connection connection;
    private final UserDao userDao;

    public LocalDataSource() {
        userDao = new UserDao(this);
    }

    @Override
    public void start() {
        try {
            connection = connect();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUser(String login, String pass) {
        return userDao.getUser(login, pass);
    }

    @Override
    public synchronized User createUser(String login, String nick, String pass) {
        return userDao.createUser(login, nick, pass) ? userDao.getUser(login, pass) : null;
    }

    @Override
    public synchronized boolean changeNick(long userId, String newNick) {
        if (userDao.getUserByNick(newNick) != null) {
            return false;
        }
        return userDao.changeNick(userId, newNick);
    }

    @Override
    public void stop() {
        try {
            userDao.close();

            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        return DriverManager.getConnection("jdbc:sqlite:main.db");
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new RuntimeException("No database connection");
        }
        return connection;
    }
}
