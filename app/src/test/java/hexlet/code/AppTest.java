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
import java.util.Map;
import java.util.Objects;

import static hexlet.code.repository.BaseRepository.dataSource;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;
    private MockWebServer mockWebServer;
    private Map<String, Object> existingUrl;
    private Map<String, Object> existingUrlCheck;

    private void configureRoutes() {
        app.get(NamedRoutes.homePath(), MainController::index);
        app.post(NamedRoutes.homePath(), MainController::addUrl);
        app.get(NamedRoutes.urlsPath(), UrlsController::showAllUrls);
        app.post(NamedRoutes.urlsPath(), UrlsController::showAllUrls);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::showUrlById);
        app.get(NamedRoutes.checksUrlPath("{id}"), UrlsController::checkUrl);
        app.post(NamedRoutes.checksUrlPath("{id}"), UrlsController::checkUrl);
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        app = App.getApp();
        configureRoutes();
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        String url = mockWebServer.url("/").toString().replaceAll("/$", "");

        TestUtils.addUrl(dataSource, url);
        existingUrl = TestUtils.getUrlByName(dataSource, url);

        assert existingUrl != null;
        TestUtils.addUrlCheck(dataSource, (long) existingUrl.get("id"));
        existingUrlCheck = TestUtils.getUrlCheck(dataSource, (long) existingUrl.get("id"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
        App.stopApp();
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
    void testIndex() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains(existingUrl.get("name").toString())
                    .contains(existingUrlCheck.get("status_code").toString());
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
    void testStore1() {
        String url1 = mockWebServer.url("/").toString().replaceAll("/$", "");
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + url1;
            assertThat(client.post("/urls", requestBody).code()).isEqualTo(200);
            assertThat(Objects.requireNonNull(client.post("/urls", requestBody)
                    .body()).string()).contains("Hello Hexlet!");

            assertThat(existingUrl).isNotNull();

            assertThat(existingUrl.get("name").toString()).isEqualTo(url1);

            client.post("/urls/" + existingUrl.get("id") + "/checks");

            assertThat(client.get("/urls/" + existingUrl.get("id")).code())
                    .isEqualTo(200);

            var actualCheck = TestUtils.getUrlCheck(dataSource, (long) existingUrl.get("id"));
            assertThat(actualCheck).isNotNull();
            assertThat(actualCheck.get("title")).isEqualTo("en title");
            assertThat(actualCheck.get("h1")).isEqualTo("en h1");
            assertThat(actualCheck.get("description")).isEqualTo("en description");
        });
    }

    @Test
    void testStore2() throws SQLException {

        String inputUrl = "https://ru.hexlet.io";
        var url = new Url("https://ru.hexlet.io", LocalDateTime.now());
        UrlsRepository.save(url);
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "url=" + inputUrl;
            assertThat(client.post("/urls", requestBody).code()).isEqualTo(200);

            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains(inputUrl);

            var actualUrl = TestUtils.getUrlByName(dataSource, inputUrl);
            assertThat(actualUrl).isNotNull();
            assertThat(actualUrl.get("name").toString()).isEqualTo(inputUrl);
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

            assertThat(client.post("/urls", requestBody).code()).isEqualTo(200);

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

    @Test
    void testFailedHttpRequest() throws SQLException {
        // Тест обработки ситуации, когда HTTP-запрос завершается неудачей
        var url = new Url("https://example.com", LocalDateTime.now());
        UrlsRepository.save(url);
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        AppTestUtils.waitForApp();
        assertThat(UrlCheckRepository.findLastByUrlId(url.getId())).isNull();
    }

    @Test
    void testIndex2() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains(existingUrl.get("name").toString())
                    .contains(existingUrlCheck.get("status_code").toString());
        });
    }

    @Test
    void testShow() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + existingUrl.get("id"));
            assertThat(response.code()).isEqualTo(200);
            assert response.body() != null;
            assertThat(response.body().string())
                    .contains(existingUrl.get("name").toString())
                    .contains(existingUrlCheck.get("status_code").toString());
        });
    }
}
