package ru.geekbrains.hw.chat.server.usecases.interactors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.server.usecases.Repository;
import ru.geekbrains.hw.chat.server.usecases.Server;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerInteractorImplTest {

    private static final String TEST_NICK = "nick";
    private static final String TEST_NICK2 = "nick2";
    private static final String TEST_MESSAGE = "test message";

    @Mock
    Repository repository;

    @Mock
    Server server;

    @Mock
    ClientHandlerInteractor client;

    @Mock
    ClientHandlerInteractor client2;

    private ServerInteractorImpl interactor;

    @BeforeEach
    public void before() {
        interactor = new ServerInteractorImpl(repository);
        interactor.setServer(server);
    }

    @Test
    void testSubscribeMethod() {
        assertFalse(interactor.isNickBusy(TEST_NICK));
        connectClient();
        assertTrue(interactor.isNickBusy(TEST_NICK));
    }

    @Test
    void testUnsubscribeMethod() {
        connectClient();
        interactor.unsubscribe(client);
        assertFalse(interactor.isNickBusy(TEST_NICK));
    }

    @Test
    void testBroadcastMethod() {
        connectClient();
        interactor.broadcast(TEST_MESSAGE);
        verify(client).sendMsg(TEST_MESSAGE);
    }

    @Test
    void testBroadcastClientsListMethod() {
        connectTwoClients();
        interactor.broadcastClientsList();
        String message = ChatCommands.SERVER_CLIENTS + " " + TEST_NICK + " " + TEST_NICK2;
        verify(client).sendMsg(message);
        verify(client2).sendMsg(message);
    }

    @Test
    void testSendMessageToClientMethod_Success() {
        connectTwoClients();
        interactor.sendMsgToClient(client, client2.getName(), TEST_MESSAGE);
        verify(client).sendMsg("to " + client2.getName() + ": " + TEST_MESSAGE);
        verify(client2).sendMsg("from " + client.getName() + ": " + TEST_MESSAGE);
    }

    @Test
    void testSendMessageToClientMethod_Failure() {
        connectClient();
        interactor.sendMsgToClient(client, TEST_NICK2, TEST_MESSAGE);
        verify(client).sendMsg(String.format("%s is not connected.", TEST_NICK2));
    }

    @Test
    void testStartMethod() {
        Mockito.doThrow(new RuntimeException()).when(server).waitForClient();
        try {
            interactor.start();
        } catch (RuntimeException ignored) {
        }
        verify(repository).start();
        verify(server).waitForClient();
        verify(repository).stop();
    }

    private void connectClient() {
        when(client.getName()).thenReturn(TEST_NICK);
        interactor.subscribe(client);
    }

    private void connectTwoClients() {
        connectClient();
        when(client2.getName()).thenReturn(TEST_NICK2);
        interactor.subscribe(client2);
    }
}
