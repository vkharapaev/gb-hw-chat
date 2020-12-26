package ru.geekbrains.hw.chat.client.usecases.interactors;

import ru.geekbrains.hw.chat.ChatCommands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ClientListInteractor {
    List<String> nickList;

    public ClientListInteractor() {
        nickList = Collections.emptyList();
    }

    public void fillList(String clientsMessage) {
        if (clientsMessage != null && clientsMessage.startsWith(ChatCommands.SERVER_CLIENTS)) {
            String[] nicks = clientsMessage.split("\\s");
            nickList = Arrays.asList(Arrays.copyOfRange(nicks, 1, nicks.length));
            nickList.sort(String.CASE_INSENSITIVE_ORDER);
        }
    }

    public List<String> getNickList() {
        return nickList;
    }
}
