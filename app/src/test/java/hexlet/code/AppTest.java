package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is designed for extension. To safely extend this class, override the methods
 * as needed, but be aware of the potential impact on the existing functionality.
 */
public class AppTest {
    private Javalin app;
    private MockWebServer mockWebServer;

    /**
     * Sets up the test environment before each test.
     *
     * @throws SQLException if there is an error setting up the database connection
     * @throws IOException  if there is an error loading the database properties
     */
    @BeforeEach
    public void setUp() throws SQLException, IOException {
//        setDatabaseConnectionParams();
        app = App.getApp(); // Ensure this creates a new instance for each test
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    /**
     * Tears down the test environment after each test.
     *
     * @throws IOException if there is an error shutting down the mock web server
     */
    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        App.stopApp(app); // Ensure this stops the current instance after each test
        app = null; // Reset the app instance to avoid reuse
    }

    @Test
    void testH2Connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:project");
        Assertions.assertNotNull(connection);
        connection.close();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.homePath());
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Hello Hexlet!");
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testCheckUrl() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.checksUrlPath(url.getId()));
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string()).contains("Example Domain");
        });
    }

    @Test
    void testCheckUrlPost1() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> assertThat(client.post(NamedRoutes.checksUrlPath(url.getId()))
                .code()).isEqualTo(200));
    }

    @Test
    void testCheckUrlPost2() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> assertThat(Objects.requireNonNull(client
                .post(NamedRoutes.checksUrlPath(url.getId()))
                .body()).string()).contains("https://example.com"));
    }

    @Test
    void testCheckUrlWithError() throws SQLException {
        var url = new Url("http://localhost:8080", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> assertThat(Objects.requireNonNull(client
                .post(NamedRoutes.checksUrlPath(url.getId()))
                .body()).string()).contains("Hello Hexlet!"));
    }

    @Test
    void testCheckUrlWithError2() throws SQLException {
        var url = new Url("http://localhost:8080", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            assertThat(client.get(NamedRoutes.checksUrlPath(url.getId())).code()).isEqualTo(200);
            assertThat(Objects.requireNonNull(client.post(NamedRoutes.checksUrlPath(url.getId()))
                    .body()).string()).contains("Запустить проверку");
        });
    }

    @Test
    void testCheckUrlWithError3() throws SQLException {
        var url = new Url("http://localhost:8080", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            assertThat(client.post(NamedRoutes.checksUrlPath(url.getId())).code()).isEqualTo(200);
            assertThat(Objects.requireNonNull(client.get(NamedRoutes.checksUrlPath(url.getId()))
                    .body()).string()).contains("Запустить проверку");
        });
    }

    @Test
    void testUrlPage() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains("Hello Hexlet!");
            assertThat(response.headers().toString())
                    .contains("Date");
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");
            assertThat(response.code()).isEqualTo(404);
        });
    }

    @Test
    public void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            var url = new Url("https://example.com", LocalDateTime.now());
            UrlsRepository.save(url);
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains("https://example.com");
        });
    }

    @Test
    void testAddUrl() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    void testDisplayUrlCheckInfo() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        // Предположим, что мы уже добавили проверку для этого URL и сохранили информацию в базе данных
        var urlCheck = new UrlCheck();
        urlCheck.setStatusCode(200);
        urlCheck.setDescription("Test Description");
        urlCheck.setUrl(url); // Установка связанного объекта Url
        UrlCheckRepository.save(urlCheck); // Предполагается, что у вас есть метод для сохранения проверки

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200); // Используйте status вместо code
            assert response.body() != null;
            // Проверка наличия информации о проверке в ответе
            assertThat(response.body().string()).contains("Test Description");
        });
    }
}
