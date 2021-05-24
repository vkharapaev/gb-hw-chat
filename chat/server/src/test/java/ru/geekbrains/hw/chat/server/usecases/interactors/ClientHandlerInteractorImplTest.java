package ru.geekbrains.hw.chat.server.usecases.interactors;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.server.TestUtils;
import ru.geekbrains.hw.chat.server.entities.User;
import ru.geekbrains.hw.chat.server.usecases.ClientHandler;
import ru.geekbrains.hw.chat.server.usecases.Repository;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientHandlerInteractorImplTest {

    private static final String INCORRECT_LOGIN_PASS = "The login/pass is not correct.";
    private static final String TEST_MESSAGE = "test message";

    @Mock
    ServerInteractor serverInteractor;

    @Mock
    ClientHandler clientHandler;

    private ClientHandlerInteractorImpl interactor;

    @BeforeEach
    void setUp() {
        interactor = new ClientHandlerInteractorImpl(serverInteractor);
        interactor.setClientHandler(clientHandler);
    }

    @Test
    void testThatClientDisconnected() {
        setUpReadMessagesMethod();
        interactor.onStart();
        verify(clientHandler).closeConnection();
        verifyNoInteractions(serverInteractor);
    }

    @Test
    void testConnectionFailedBecauseOfWrongRequest() {
        setUpReadMessagesMethod(ChatCommands.CLIENT_AUTH + " login");
        interactor.onStart();
        verify(clientHandler).sendMsg(INCORRECT_LOGIN_PASS);
    }

    @Test
    void testConnectionFailedBecauseClientDoesNotExist() {
        setUpReadMessagesMethod(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"));
        when(serverInteractor.getUserRepository()).thenReturn(mock(Repository.class));
        interactor.onStart();
        verify(clientHandler).sendMsg(INCORRECT_LOGIN_PASS);
    }

    @Test
    void testSuccessfulAuthorizationAndThenDisconnection() {
        setUpReadMessagesMethod(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"));
        Repository repository = mock(Repository.class);
        User user = TestUtils.createUser();
        when(repository.getUser(any(), any())).thenReturn(user);
        when(serverInteractor.getUserRepository()).thenReturn(repository);
        when(serverInteractor.subscribe(any())).thenReturn(true);
        interactor.onStart();
        InOrder order = inOrder(clientHandler, serverInteractor);
        order.verify(clientHandler).setTimer();
        order.verify(serverInteractor).subscribe(interactor);
        order.verify(clientHandler).sendMsg(ChatCommands.SERVER_AUTH_OK + " " + user.getNick());
        order.verify(serverInteractor).broadcastClientsList();
        order.verify(serverInteractor).broadcast(user.getNick() + " entered the chat");
        order.verify(clientHandler).cancelTimer();
        order.verify(serverInteractor).unsubscribe(interactor);
        order.verify(serverInteractor).broadcast(user.getNick() + " has left the chat.");
        order.verify(serverInteractor).broadcastClientsList();
    }

    @Test
    void testAccountInUse() {
        setUpReadMessagesMethod(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"));
        Repository repository = mock(Repository.class);
        User user = TestUtils.createUser();
        when(repository.getUser(any(), any())).thenReturn(user);
        when(serverInteractor.getUserRepository()).thenReturn(repository);
        when(serverInteractor.subscribe(any())).thenReturn(false);
        interactor.onStart();
        InOrder order = inOrder(clientHandler, serverInteractor);
        order.verify(serverInteractor).subscribe(interactor);
        order.verify(clientHandler).sendMsg("The account is already in use.");
    }

    @Test
    void testSendMessageToChat() {
        setUpReadMessagesMethod(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"), TEST_MESSAGE);
        Repository repository = mock(Repository.class);
        User user = TestUtils.createUser();
        when(repository.getUser(any(), any())).thenReturn(user);
        when(serverInteractor.getUserRepository()).thenReturn(repository);
        when(serverInteractor.subscribe(any())).thenReturn(true);
        interactor.onStart();
        InOrder order = inOrder(clientHandler, serverInteractor);
        order.verify(serverInteractor).broadcast(String.format("%s: %s", user.getNick(), TEST_MESSAGE));
    }

    @Test
    void testSendMessageToClient() {
        User user = TestUtils.createUser();
        User user2 = TestUtils.createUser2();
        setUpReadMessagesMethod(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"),
                ChatCommands.CLIENT_PRIVATE_MSG + " " + user2.getNick() + " " + TEST_MESSAGE);
        Repository repository = mock(Repository.class);
        when(repository.getUser(any(), any())).thenReturn(user);
        when(serverInteractor.getUserRepository()).thenReturn(repository);
        when(serverInteractor.subscribe(any())).thenReturn(true);
        interactor.onStart();
        verify(serverInteractor).sendMsgToClient(interactor, user2.getNick(), TEST_MESSAGE);
    }

    @Test
    void testChangeNick() {
        User user = TestUtils.createUser();
        setUpReadMessagesMethod(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"),
                ChatCommands.CLIENT_CHANGE_NICK + " newnick");
        Repository repository = mock(Repository.class);
        when(repository.getUser(any(), any())).thenReturn(user);
        when(serverInteractor.getUserRepository()).thenReturn(repository);
        when(serverInteractor.subscribe(any())).thenReturn(true);
        interactor.onStart();
        verify(repository).changeNick(user.getId(), "newnick");
    }

    @SneakyThrows
    void setUpReadMessagesMethod(String... messages) {
        OngoingStubbing<String> stubbing = when(clientHandler.readMessage());
        for (String message : messages) {
            stubbing = stubbing.thenReturn(message);
        }
        stubbing.thenThrow(IOException.class);
    }
}