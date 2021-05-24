package ru.geekbrains.hw.chat.client.utils;

import java.util.concurrent.BlockingQueue;

public interface MessageQueueFactory {
    MessageQueue create(BlockingQueue<String> queue);
}
