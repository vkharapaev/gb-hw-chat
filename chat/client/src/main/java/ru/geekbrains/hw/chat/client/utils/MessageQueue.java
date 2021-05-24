package ru.geekbrains.hw.chat.client.utils;

public interface MessageQueue {

    void start(MessageQueue.OnReadMessage reader);

    void stop();

    interface OnReadMessage {
        void read(String message);
    }
}
