package ru.geekbrains.hw.chat.client.utils;

import java.util.concurrent.BlockingQueue;

public class MessageQueueFactoryImpl implements MessageQueueFactory {
    @Override
    public MessageQueue create(BlockingQueue<String> queue) {
        return new MessageQueueImpl(queue);
    }
}
