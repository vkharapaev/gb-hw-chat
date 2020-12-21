package ru.geekbrains.hw.chat.client;

import java.util.concurrent.BlockingQueue;

public class MessageQueue {

    private final BlockingQueue<String> messageQueue;
    private Thread thread;

    public MessageQueue(BlockingQueue<String> messageQueue) {
        this.messageQueue = messageQueue;
    }

    public void start(OnReadMessage reader) {
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

    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }

    public interface OnReadMessage {
        void read(String message);
    }
}
