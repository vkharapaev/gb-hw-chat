package ru.geekbrains.hw.chat.server.frameworks.local;

import ru.geekbrains.hw.chat.server.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class UserDao extends Dao {
    private static final String SELECT_USER = "SELECT * FROM " + DBHelper.TABLE_USER + " WHERE " +
            DBHelper.COLUMN_LOGIN + " = ? AND " + DBHelper.COLUMN_PASS + " = ?";
    private static final int SELECT_USER_LOGIN_PARAM = 1;
    private static final int SELECT_USER_PASS_PARAM = 2;

    private static final String SELECT_USER_BY_NICK = "SELECT * FROM " + DBHelper.TABLE_USER + " WHERE " +
            DBHelper.COLUMN_NICK + " = ?";
    private static final int SELECT_USER_BY_NICK_NICK_PARAM = 1;

    private static final String UPDATE_USER_NICK = "UPDATE " + DBHelper.TABLE_USER + " SET " + DBHelper.COLUMN_NICK +
            " = ? WHERE " + DBHelper.COLUMN_USER_ID + " = ?";
    private static final int UPDATE_USER_NICK_NICK_PARAM = 1;
    private static final int UPDATE_USER_NICK_ID_PARAM = 2;

    private static final String CREATE_USER = "INSERT INTO " + DBHelper.TABLE_USER + " (" + DBHelper.COLUMN_LOGIN + ", "
            + DBHelper.COLUMN_NICK + ", " + DBHelper.COLUMN_PASS + ") VALUES (?, ?, ?)";
    private static final int CREATE_USER_LOGIN_PARAM = 1;
    private static final int CREATE_USER_NICK_PARAM = 2;
    private static final int CREATE_USER_PASS_PARAM = 3;

    private PreparedStatement selectUserStatement;
    private PreparedStatement selectUserByNickStatement;
    private PreparedStatement updateUserNickStatement;
    private PreparedStatement createUserStatement;

    public UserDao(LocalDataSource dataSource) {
        super(dataSource);
    }

    User getUser(String login, String pass) {
        try {
            PreparedStatement statement = getSelectUserStatement();
            statement.setString(SELECT_USER_LOGIN_PARAM, login);
            statement.setString(SELECT_USER_PASS_PARAM, pass);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    User getUserByNick(String nick) {
        try {
            PreparedStatement statement = getSelectUserByNickStatement();
            statement.setString(SELECT_USER_BY_NICK_NICK_PARAM, nick);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean changeNick(long userId, String newNick) {
        try {
            PreparedStatement statement = getUpdateUserNickStatement();
            statement.setString(UPDATE_USER_NICK_NICK_PARAM, newNick);
            statement.setLong(UPDATE_USER_NICK_ID_PARAM, userId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createUser(String login, String nick, String pass) {
        try {
            PreparedStatement statement = getCreateUserStatement();
            statement.setString(CREATE_USER_LOGIN_PARAM, login);
            statement.setString(CREATE_USER_NICK_PARAM, nick);
            statement.setString(CREATE_USER_PASS_PARAM, pass);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User mapUser(ResultSet userResultSet) throws SQLException {
        return User.builder()
                .id(userResultSet.getLong(DBHelper.COLUMN_USER_ID))
                .login(userResultSet.getString(DBHelper.COLUMN_LOGIN))
                .pass(userResultSet.getString(DBHelper.COLUMN_PASS))
                .nick(userResultSet.getString(DBHelper.COLUMN_NICK))
                .build();
    }

    void close() throws SQLException {
        if (selectUserStatement != null) {
            selectUserStatement.close();
        }
        if (selectUserByNickStatement != null) {
            selectUserByNickStatement.close();
        }
        if (updateUserNickStatement != null) {
            updateUserNickStatement.close();
        }
    }

    private PreparedStatement getSelectUserStatement() throws SQLException {
        if (selectUserStatement == null) {
            selectUserStatement = getConnection().prepareStatement(SELECT_USER);
        }
        return selectUserStatement;
    }

    private PreparedStatement getSelectUserByNickStatement() throws SQLException {
        if (selectUserByNickStatement == null) {
            selectUserByNickStatement = getConnection().prepareStatement(SELECT_USER_BY_NICK);
        }
        return selectUserByNickStatement;
    }

    private PreparedStatement getUpdateUserNickStatement() throws SQLException {
        if (updateUserNickStatement == null) {
            updateUserNickStatement = getConnection().prepareStatement(UPDATE_USER_NICK);
        }
        return updateUserNickStatement;
    }

    private PreparedStatement getCreateUserStatement() throws SQLException {
        if (createUserStatement == null) {
            createUserStatement = getConnection().prepareStatement(CREATE_USER);
        }
        return createUserStatement;
    }
}
