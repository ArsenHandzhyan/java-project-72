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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;
    private MockWebServer mockWebServer;

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        app = App.getApp();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        App.stopApp(app);
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
