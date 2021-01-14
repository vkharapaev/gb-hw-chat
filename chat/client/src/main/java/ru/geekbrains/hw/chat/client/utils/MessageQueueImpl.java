package ru.geekbrains.hw.chat.client.utils;

import java.util.concurrent.BlockingQueue;

public class MessageQueueImpl implements MessageQueue {

    private final BlockingQueue<String> messageQueue;
    private Thread thread;

    public MessageQueueImpl(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    @Override
    public void start(MessageQueue.OnReadMessage reader) {
        thread = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    reader.read(messageQueue.take());
                }
            } catch (InterruptedException ignore) {
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }
}
