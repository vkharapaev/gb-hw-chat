package ru.geekbrains.hw.chat.client.usecases.interactors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.client.usecases.Client;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;
import ru.geekbrains.hw.chat.client.utils.MessageQueueFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientInteractorImplTest {

    private static final String TEST_MSG = "test message 1";
    private static final String TEST_MSG2 = "test message 2";

    @Mock
    HistoryInteractor historyInteractor;

    @Mock
    Client client;

    @Captor
    ArgumentCaptor<Runnable> runnableCaptor;

    private ClientInteractorImpl interactor;

    @BeforeEach
    public void before() {
        interactor = new ClientInteractorImpl(new TestMQFactory(), historyInteractor);
        interactor.setClient(client);
    }

    @Test
    public void testBrokenConnectionAtAuthorizationStage() {
        setUpReadMessagesMethod();
        startClient();
        verify(client).closeConnection();
        checkQueue(getMessageQueue(), ClientInteractor.MSG_END_CONNECTION);
    }

    @Test
    public void testBrokenConnectionAtChatStage() {
        setUpReadMessagesMethod(ChatCommands.SERVER_AUTH_OK);
        startClient();
        verify(client).closeConnection();
        checkQueue(getMessageQueue(), ClientInteractor.MSG_END_AUTH, ClientInteractor.MSG_END_CONNECTION);
    }

    @Test
    public void testReadMessages() {
        setUpReadMessagesMethod(ChatCommands.SERVER_AUTH_OK, TEST_MSG, TEST_MSG2);
        startClient();
        checkQueue(getMessageQueue(), ClientInteractor.MSG_END_AUTH, TEST_MSG, TEST_MSG2,
                ClientInteractor.MSG_END_CONNECTION);
    }

    @Test
    public void testReadMessages_ClientsCommand() {
        String message = "/clients nick, nick2, nick3";
        setUpReadMessagesMethod(ChatCommands.SERVER_AUTH_OK, message);
        startClient();
        checkQueue(getClientsQueue(), message);
    }

    @Test
    public void testStoreMessageInHistory() {
        setUpReadMessagesMethod(ChatCommands.SERVER_AUTH_OK, TEST_MSG);
        startClient();
        verify(historyInteractor).storeMessage(TEST_MSG);
    }

    @Test
    public void testShowHistory() {
        setUpReadMessagesMethod(ChatCommands.SERVER_AUTH_OK);
        when(historyInteractor.getHistory(anyInt()))
                .thenReturn(Collections.singletonList(TEST_MSG));
        startClient();
        verify(historyInteractor).setSourceName(any());
        verify(historyInteractor).getHistory(anyInt());
        checkQueue(getMessageQueue(), ClientInteractor.MSG_END_AUTH, TEST_MSG,
                ClientInteractor.MSG_END_CONNECTION);
    }

    @Test
    public void testSignIn_cannot_connect_to_server() {
        when(client.isConnectionClosed()).thenReturn(true);
        interactor.signIn("login", "pass").subscribe();
        verify(client).start(any());
        verify(client, times(2)).isConnectionClosed();
        checkQueue(getMessageQueue(), "The server is not responding.");
    }

    @Test
    public void testSignIn_successful_connection() {
        when(client.isConnectionClosed()).thenReturn(true, false);
        interactor.signIn("login", "pass").subscribe();
        verify(client).start(any());
        verify(client, times(2)).isConnectionClosed();
        verify(client).sendMsg(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"));
    }

    @Test
    public void testSignIn_reconnection_after_successful_connection() {
        when(client.isConnectionClosed()).thenReturn(false);
        interactor.signIn("login", "pass").subscribe();
        verify(client, never()).start(any());
        verify(client, times(2)).isConnectionClosed();
        verify(client).sendMsg(String.format(ChatCommands.CLIENT_TEMPLATE_AUTH, "login", "pass"));
    }

    @Test
    public void testSignIn_successful_registration() {
        when(client.isConnectionClosed()).thenReturn(true, false);
        interactor.register("login", "nick", "pass").subscribe();
        verify(client).start(any());
        verify(client, times(2)).isConnectionClosed();
        verify(client).sendMsg(String.format(ChatCommands.CLIENT_TEMPLATE_REG, "login", "nick", "pass"));
    }

    private Queue<String> getMessageQueue() {
        return ((TestMQ) interactor.getMessageQueue()).mq;
    }

    private Queue<String> getClientsQueue() {
        return ((TestMQ) interactor.getClientsMessageQueue()).mq;
    }

    private void setUpReadMessagesMethod(String... messages) {
        try {
            OngoingStubbing<String> stubbing = when(client.readMessage());
            for (String message : messages) {
                stubbing = stubbing.thenReturn(message);
            }
            stubbing.thenThrow(new IOException());
        } catch (IOException ignored) {
        }
    }

    private void checkQueue(Queue<String> queue, String... messages) {
        for (String message : messages) {
            assertEquals(queue.poll(), message);
        }
        assertTrue(queue.isEmpty());
    }

    private void startClient() {
        interactor.start();
        verify(client).start(runnableCaptor.capture());
        runnableCaptor.getValue().run();
    }

    private static final class TestMQFactory implements MessageQueueFactory {
        @Override
        public MessageQueue create(BlockingQueue<String> mq) {
            return new TestMQ(mq);
        }
    }

    private static final class TestMQ implements MessageQueue {
        public BlockingQueue<String> mq;

        public TestMQ(BlockingQueue<String> mq) {
            this.mq = mq;
        }

        @Override
        public void start(OnReadMessage reader) {
        }

        @Override
        public void stop() {
        }
    }
}
