package ru.geekbrains.hw.chat;

public class ChatMessages {
    public static final String SERVER_MSG_AUTH_OK = "/authok";
    public static final String SERVER_MSG_CLIENTS = "/clients";
    public static final String CLIENT_MSG_AUTH = "/auth";
    public static final String CLIENT_MSG_TEMPLATE_AUTH = CLIENT_MSG_AUTH + " %s %s";
    public static final String CLIENT_MSG_REG = "/reg";
    public static final String CLIENT_MSG_TEMPLATE_REG = CLIENT_MSG_REG + " %s %s %s";
    public static final String CLIENT_MSG_END = "/end";
    public static final String CLIENT_MSG_PRIVATE_MSG = "/w";
    public static final String CLIENT_MSG_CHANGE_NICK = "/cn";
}
