package ru.geekbrains.hw.chat.client.adapters.presenters;

public interface BasePresenter<T> {

    /**
     * Set a view to a presenter
     *
     * @param view The view associated with this presenter
     */
    void takeView(T view);
}
