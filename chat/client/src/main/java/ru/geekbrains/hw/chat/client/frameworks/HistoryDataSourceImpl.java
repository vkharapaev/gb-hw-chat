package ru.geekbrains.hw.chat.client.frameworks;

import ru.geekbrains.hw.chat.client.adapters.data.HistoryDataSource;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HistoryDataSourceImpl implements HistoryDataSource {

    private static final String FILE_NAME_TEMPLATE = "history_%s.txt";
    private static final int READ_BLOCK_SIZE = 8192;
    private String sourceName;

    @Override
    public void storeMessage(String message) {
        try (Writer writer = getWriter()) {
            if (getFile().length() > 0) {
                writer.append("\n");
            }
            writer.append(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getHistory(int count) {
        if (!getFile().exists()) {
            return Collections.emptyList();
        }
        String[] messages = {};
        try (FileChannel channel = new FileInputStream(getFile()).getChannel()) {
            long fileSize = channel.size();
            long blockSize = 0;
            while (blockSize < fileSize && messages.length <= count) {
                blockSize += Math.min(Math.max(fileSize - blockSize, 0), READ_BLOCK_SIZE);
                long position = fileSize - blockSize;
                ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, position, blockSize);
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);
                messages = new String(bytes).split("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (messages.length > count) {
            messages = Arrays.copyOfRange(messages, messages.length - count, messages.length);
        }
        return Arrays.asList(messages);
    }

    @Override
    public void setSourceName(String name) {
        sourceName = name;
    }

    private File getFile() {
        if (sourceName == null) {
            throw new RuntimeException("Specify the source name first");
        }
        return new File(String.format(FILE_NAME_TEMPLATE, sourceName));
    }

    private Writer getWriter() throws IOException {
        return new BufferedWriter(new FileWriter(getFile(), true));
    }
}
