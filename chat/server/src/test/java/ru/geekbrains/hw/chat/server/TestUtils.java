package ru.geekbrains.hw.chat.server;

import ru.geekbrains.hw.chat.server.entities.User;

public class TestUtils {

    public static final long USER_ID = 1;
    public static final String USER_LOGIN = "login";
    public static final String USER_PASS = "pass";
    public static final String USER_NICK = "nick";

    public static final long USER_ID2 = 2;
    public static final String USER_LOGIN2 = "login2";
    public static final String USER_PASS2 = "pass2";
    public static final String USER_NICK2 = "nick2";

    public static User createUser() {
        return User.builder().id(USER_ID).login(USER_LOGIN).pass(USER_PASS).nick(USER_NICK).build();
    }

    public static User createUser2() {
        return User.builder().id(USER_ID2).login(USER_LOGIN2).pass(USER_PASS2).nick(USER_NICK2).build();
    }
}
