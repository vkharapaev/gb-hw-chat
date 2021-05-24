package ru.geekbrains.hw.chat.client.adapters.presenters.reg;

import io.reactivex.Completable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.geekbrains.hw.chat.client.adapters.presenters.TestSchedulers;
import ru.geekbrains.hw.chat.client.adapters.presenters.login.LoginContract;
import ru.geekbrains.hw.chat.client.adapters.presenters.login.LoginPresenter;
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.utils.JavaFx;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegPresenterTest {
    @Mock
    ClientInteractor interactor;

    @Mock
    JavaFx javaFx;

    @Mock
    RegContract.View view;

    @Mock
    MessageQueue messageQueue;

    @Captor
    ArgumentCaptor<MessageQueue.OnReadMessage> readMessageCaptor;

    @Captor
    ArgumentCaptor<Runnable> runnableCaptor;

    private RegPresenter presenter;


    @BeforeEach
    public void before() {
        presenter = new RegPresenter(interactor, javaFx, new TestSchedulers());
        when(interactor.getMessageQueue()).thenReturn(messageQueue);
        presenter.takeView(view);
    }

    @Test
    public void readMessages_goToChatWindow() {
        verify(messageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read(ClientInteractor.MSG_END_AUTH);
        verify(messageQueue).stop();
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(view).goToChatWindow();
    }

    @Test
    public void readMessages_showError() {
        verify(messageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read("error message");
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(view).showError("error message");
    }

    @CsvSource({",,", "login,,", ",pass,", ",,nick", "login,pass,", ",pass,nick", "login,,nick"})
    @ParameterizedTest
    public void testRegister_Failed(String login, String pass, String nick) {
        presenter.register(login, pass, nick);
        verify(view).showError("Please, fill in all fields");
    }

    @Test
    public void testGoBack() {
        presenter.goBack();
        verify(view).goToLoginWindow();
    }

    @Test
    public void testLogIn_Successful() {
        when(interactor.register(any(), any(), any())).thenReturn(Completable.complete());
        presenter.register("login", "pass", "nick");
        verify(view).showError("Connecting...");
    }

}