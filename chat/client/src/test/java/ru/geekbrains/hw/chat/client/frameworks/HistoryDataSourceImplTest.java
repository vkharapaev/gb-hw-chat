package ru.geekbrains.hw.chat.client.frameworks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class HistoryDataSourceImplTest {
    private static final String TEST_MESSAGE = "test message";
    private static final String TEST_MESSAGE2 = "test message 2";
    public static final String SOURCE_NAME = "login";
    public static final String FILE_NAME = String.format("history_%s.txt", SOURCE_NAME);

    private HistoryDataSourceImpl dataSource;

    @BeforeEach
    void before() {
        dataSource = new HistoryDataSourceImpl();
        new File(FILE_NAME).delete();
    }

    @AfterEach
    void after() {
        new File(FILE_NAME).delete();
    }

    @Test
    public void testStoreMessageWithoutSourceName() {
        assertThrows(RuntimeException.class, () -> dataSource.storeMessage(TEST_MESSAGE));
    }

    @Test
    void testStoreMessage() {
        dataSource.setSourceName(SOURCE_NAME);
        dataSource.storeMessage(TEST_MESSAGE);
        dataSource.storeMessage(TEST_MESSAGE2);
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ( (line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(Arrays.asList(TEST_MESSAGE, TEST_MESSAGE2), lines);
    }

    @Test
    void testReadLastNLines() {
        dataSource.setSourceName(SOURCE_NAME);
        List<String> expected = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String message = i + " " + TEST_MESSAGE;
            dataSource.storeMessage(message);
            if (i > 5) {
                expected.add(message);
            }
        }
        List<String> history = dataSource.getHistory(5);
        assertEquals(expected, history);
    }
}