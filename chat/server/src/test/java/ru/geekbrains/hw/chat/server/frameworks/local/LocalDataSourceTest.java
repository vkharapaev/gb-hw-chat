package ru.geekbrains.hw.chat.server.frameworks.local;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.geekbrains.hw.chat.server.entities.User;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocalDataSourceTest {

    public static final String DB_NAME = "main.db";
    public static final String TEST_DB_NAME = "test-main.db";
    private LocalDataSource dataSource;

    @BeforeEach
    void setUp() throws IOException {
        new File(TEST_DB_NAME).delete();
        Files.copy(Paths.get(DB_NAME), Paths.get(TEST_DB_NAME), StandardCopyOption.REPLACE_EXISTING);
        dataSource = new LocalDataSource(TEST_DB_NAME);
        dataSource.start();
    }

    @AfterEach
    void tearDown() {
        dataSource.stop();
        new File(TEST_DB_NAME).delete();
    }

    @Test
    void testGetUser() {
        User user = dataSource.getUser("login", "pass");
        assertNotNull(user);
        assertEquals(user.getLogin(), "login");
        assertEquals(user.getPass(), "pass");
    }

    @Test
    void testChangeNick() {
        User user = dataSource.getUser("login", "pass");
        assertEquals("nick", user.getNick());
        dataSource.changeNick(user.getId(), "newnick");
        user = dataSource.getUser("login", "pass");
        assertEquals("newnick", user.getNick());
    }

    @Test
    void testCreateUser() {
        assertNull(dataSource.getUser("login5", "pass5"));
        dataSource.createUser("login5", "nick5", "pass5");
        assertNotNull(dataSource.getUser("login5", "pass5"));
    }

    @Test
    void testCreateUserWithExistingLogin() {
        assertNull(dataSource.createUser("login", "nick", "pass"));
    }

}
