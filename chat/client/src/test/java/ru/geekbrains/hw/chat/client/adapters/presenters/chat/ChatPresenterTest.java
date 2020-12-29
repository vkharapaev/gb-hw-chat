package ru.geekbrains.hw.chat.client.adapters.presenters.chat;

import io.reactivex.Completable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.geekbrains.hw.chat.ChatCommands;
import ru.geekbrains.hw.chat.client.adapters.presenters.TestSchedulers;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.utils.JavaFx;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatPresenterTest {
    @Mock
    ClientInteractor interactor;

    @Mock
    JavaFx javaFx;

    @Mock
    ChatContract.View view;

    @Mock
    MessageQueue messageQueue;

    @Mock
    MessageQueue clientsMessageQueue;

    @Captor
    ArgumentCaptor<MessageQueue.OnReadMessage> readMessageCaptor;

    @Captor
    ArgumentCaptor<Runnable> runnableCaptor;

    private ChatPresenter presenter;

    @BeforeEach
    public void before() {
        presenter = new ChatPresenter(interactor, javaFx, new TestSchedulers());
        when(interactor.getMessageQueue()).thenReturn(messageQueue);
        when(interactor.getClientsMessageQueue()).thenReturn(clientsMessageQueue);
        presenter.takeView(view);
    }

    @Test
    public void testReadMessages_goToLoginWindow() {
        verify(messageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read(ClientInteractor.MSG_END_CONNECTION);
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(interactor).getMessageQueue();
        verify(interactor).getClientsMessageQueue();

        verify(messageQueue).stop();
        verify(clientsMessageQueue).stop();
        verify(view).goToLoginWindow();

        verifyNoMoreInteractions(interactor);
        verifyNoMoreInteractions(view);
        verifyNoMoreInteractions(messageQueue);
        verifyNoMoreInteractions(javaFx);
    }

    @Test
    public void testAppendMessageToChat() {
        verify(messageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read("test message");
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(view).appendToChat("test message\n");

        verifyNoMoreInteractions(interactor);
        verifyNoMoreInteractions(view);
        verifyNoMoreInteractions(messageQueue);
        verifyNoMoreInteractions(javaFx);
    }

    @Test
    public void testReadClients() {
        verify(clientsMessageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read(ChatCommands.SERVER_CLIENTS + " nick nick2");
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();

        verify(view).showClients(Arrays.asList("nick", "nick2"));

        verifyNoMoreInteractions(interactor);
        verifyNoMoreInteractions(view);
        verifyNoMoreInteractions(clientsMessageQueue);
        verifyNoMoreInteractions(javaFx);
    }

    @Test
    public void testSendMessage() {
        when(interactor.sendMessage(any())).thenReturn(Completable.complete());
        presenter.sendMessage("test message");
        verify(view).clearMessageField();
        verifyNoMoreInteractions(interactor);
        verifyNoMoreInteractions(view);
    }
}