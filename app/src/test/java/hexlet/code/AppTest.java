package hexlet.code;

import hexlet.code.controller.MainController;
import hexlet.code.controller.UrlsController;
import hexlet.code.model.Url;
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

import static hexlet.code.repository.BaseRepository.dataSource;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;
    private MockWebServer mockWebServer;

    private void configureRoutes() {
        app.get(NamedRoutes.homePath(), MainController::index);
        app.post(NamedRoutes.homePath(), MainController::addUrl);
        app.get(NamedRoutes.urlsPath(), UrlsController::showAllUrls);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::showUrlById);
        app.get(NamedRoutes.checksUrlPath("{id}"), UrlsController::checkUrl);
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        app = App.getApp();
        configureRoutes();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
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
        System.out.println(UrlsRepository.find(url.getId()));
        JavalinTest.test(app, (server, client) -> assertThat(client.get(NamedRoutes.checksUrlPath(url.getId()))
                .code()).isEqualTo(200));
    }

    @Test
    void testCheckUrlPost2() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        System.out.println(UrlsRepository.find(url.getId()));
        JavalinTest.test(app, (server, client) -> assertThat(Objects.requireNonNull(client
                .get(NamedRoutes.checksUrlPath(url.getId()))
                .body()).string()).contains("https://example.com"));
    }

    @Test
    void testCheckUrlWithError() throws SQLException {
        var url = new Url("http://localhost:8080", LocalDateTime.now());
        UrlsRepository.save(url);
        System.out.println(UrlsRepository.find(url.getId()));
        JavalinTest.test(app, (server, client) -> assertThat(Objects.requireNonNull(client
                .post(NamedRoutes.checksUrlPath(url.getId()))
                .body()).string()).contains("Not Found"));
    }

    @Test
    void testCheckUrlWithError2() throws SQLException {
        var url = new Url("http://localhost:8080", LocalDateTime.now());
        UrlsRepository.save(url);
        System.out.println(UrlsRepository.find(url.getId()));
        JavalinTest.test(app, (server, client) -> {
            assertThat(client.get(NamedRoutes.checksUrlPath(url.getId())).code()).isEqualTo(200);
            assertThat(client.post(NamedRoutes.checksUrlPath(url.getId())).code()).isEqualTo(404);
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
    void testAddUrl() throws SQLException {
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());
            assertThat(response.code()).isEqualTo(200);
        });
    }


    @Test
    void testStore() {

        String inputUrl = "https://ru.hexlet.io";

        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + inputUrl;
            assertThat(client.post("/", requestBody).code()).isEqualTo(200);

            var actualUrl = TestUtils.getUrlByName(dataSource, inputUrl);
            assert actualUrl != null;
            assertThat(actualUrl.get("name").toString()).isEqualTo(inputUrl);

            assertThat(client.post("/urls", requestBody).code()).isEqualTo(404);

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains(inputUrl);

            var actualUrl2 = TestUtils.getUrlByName(dataSource, inputUrl);
            assertThat(actualUrl2).isNotNull();
            assertThat(actualUrl.get("name").toString()).isEqualTo(inputUrl);
        });
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        App.stopApp();
    }

    @Test
    void testFailedHttpRequest() throws SQLException {
        // Тест обработки ситуации, когда HTTP-запрос завершается неудачей
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        AppTestUtils.waitForApp();
        assertThat(UrlCheckRepository.findLastByUrlId(url.getId())).isNull();
    }


}
