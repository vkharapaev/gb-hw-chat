package ru.geekbrains.hw.chat.server.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class User {
    final private long id;
    final private String login;
    @NonNull
    private String pass;
    @NonNull
    private String nick;
}

