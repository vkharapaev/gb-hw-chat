package ru.geekbrains.hw.chat.client.adapters.presenters.login;

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
import ru.geekbrains.hw.chat.client.usecases.interactors.ClientInteractor;
import ru.geekbrains.hw.chat.client.utils.JavaFx;
import ru.geekbrains.hw.chat.client.utils.MessageQueue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginPresenterTest {

    @Mock
    ClientInteractor interactor;

    @Mock
    JavaFx javaFx;

    @Mock
    LoginContract.View view;

    @Mock
    MessageQueue messageQueue;

    @Captor
    ArgumentCaptor<MessageQueue.OnReadMessage> readMessageCaptor;

    @Captor
    ArgumentCaptor<Runnable> runnableCaptor;

    private LoginPresenter presenter;

    @BeforeEach
    public void before() {
        presenter = new LoginPresenter(interactor, javaFx, new TestSchedulers());
        when(interactor.getMessageQueue()).thenReturn(messageQueue);
        presenter.takeView(view);
    }

    @Test
    public void readMessages_goToAuthWindow() {
        verify(messageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read(ClientInteractor.MSG_END_AUTH);
        verify(messageQueue).stop();
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(view).goToAuthWindow();
    }

    @Test
    public void readMessages_showError() {
        verify(messageQueue).start(readMessageCaptor.capture());
        readMessageCaptor.getValue().read("error message");
        verify(javaFx).runLater(runnableCaptor.capture());
        runnableCaptor.getValue().run();
        verify(view).showError("error message");
    }

    @CsvSource({",", "login,", ",pass"})
    @ParameterizedTest
    public void testLogIn_Failed(String login, String pass) {
        presenter.logIn(login, pass);
        verify(view).showError("Please, enter a login and password");
    }

    @Test
    public void testLogIn_Successful() {
        when(interactor.signIn(any(), any())).thenReturn(Completable.complete());
        presenter.logIn("login", "pass");
        verify(view).showError("Connecting...");
    }

}