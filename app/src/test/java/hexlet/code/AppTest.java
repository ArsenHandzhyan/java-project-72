package hexlet.code;

import hexlet.code.model.Url;
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
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;
    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        setDatabaseConnectionParams();
        app = App.getApp(); // Убедитесь, что это создает новый экземпляр для каждого теста
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        App.stopApp(app); // Убедитесь, что это останавливает текущий экземпляр после каждого теста
        app = null; // Сбросьте экземпляр app в null, чтобы избежать его повторного использования
    }

    private void setDatabaseConnectionParams() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new IllegalArgumentException("database.properties file not found");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database.properties", e);
        }

        String jdbcUrl = properties.getProperty("JDBC_DATABASE_URL");
        String username = properties.getProperty("DB_USERNAME");
        String password = properties.getProperty("DB_PASSWORD");

        System.setProperty("JDBC_DATABASE_URL", jdbcUrl);
        System.setProperty("DB_USERNAME", username);
        System.setProperty("DB_PASSWORD", password);
    }

    @Test
    void testH2Connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:project");
        Assertions.assertNotNull(connection);
        connection.close();
    }

    @Test
    void testPostgresConnection() throws SQLException {
        String jdbcUrl = System.getProperty("JDBC_DATABASE_URL");
        String username = System.getProperty("DB_USERNAME");
        String password = System.getProperty("DB_PASSWORD");

        if (jdbcUrl != null
                && !jdbcUrl.isEmpty()
                && username != null
                && !username.isEmpty()
                && password != null
                && !password.isEmpty()) {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            Assertions.assertNotNull(connection);
            connection.close();
        } else {
            Assertions.fail("Database connection parameters are not set.");
        }
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
            assertThat(response.code()).isEqualTo(200);
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
}
