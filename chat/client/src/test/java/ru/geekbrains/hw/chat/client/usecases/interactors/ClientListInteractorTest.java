package ru.geekbrains.hw.chat.client.usecases.interactors;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static ru.geekbrains.hw.chat.ChatCommands.SERVER_CLIENTS;

class ClientListInteractorTest {

    @ParameterizedTest
    @MethodSource("dataForParsingOfServerCommand")
    public void testParsingOfServerCommand(String serverCommand, List<String> result) {
        ClientListInteractor interactor = new ClientListInteractor();
        interactor.fillList(serverCommand);
        assertLinesMatch(result, interactor.getNickList());
    }

    private static Stream<Arguments> dataForParsingOfServerCommand() {
        List<Arguments> list = new ArrayList<>();
        list.add(arguments(SERVER_CLIENTS + " nick nick2 nick3", asList("nick", "nick2", "nick3")));
        list.add(arguments(null, Collections.emptyList()));
        return list.stream();
    }

}