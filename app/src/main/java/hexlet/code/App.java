package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controller.MainController;
import hexlet.code.controller.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static Javalin app;

    public static void main(String[] args) throws SQLException, IOException {
        startApp();
    }

    public static void startApp() throws SQLException, IOException {
        app = getApp();
        configureRoutes();
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        app.start(port);
    }

    public static void stopApp() {
        if (app != null) {
            app.stop();
        }
    }

    public static Javalin getApp() throws SQLException, IOException {
        BaseRepository.dataSource = initializeDataSource();
        JavalinJte.init(createTemplateEngine());
        return Javalin.create();
    }

    private static void configureRoutes() {
        app.get(NamedRoutes.homePath(), MainController::index);
        app.get(NamedRoutes.urlsPath(), UrlsController::showAllUrls);
        app.post(NamedRoutes.urlsPath(), MainController::addUrl);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::showUrlById);
        app.post(NamedRoutes.checksUrlPath("{id}"), UrlsController::checkUrl);
    }

    private static HikariDataSource initializeDataSource() throws SQLException, IOException {
        String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
        String username = System.getenv("user");
        String password = System.getenv("password");

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = "jdbc:h2:mem:project";
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        try {
            var dataSource = createDataSource(hikariConfig);
            executeSqlScript(dataSource);
            return dataSource;
        } catch (SQLException | IOException e) {
            LOGGER.error("An error occurred while initializing data source", e);
            throw e;
        }
    }

    private static HikariDataSource createDataSource(HikariConfig hikariConfig) {
        return new HikariDataSource(hikariConfig);
    }

    private static void executeSqlScript(HikariDataSource dataSource) throws SQLException, IOException {
        try (var connection = dataSource.getConnection();
             var inputStream = App.class.getClassLoader().getResourceAsStream("schema.sql")) {
            assert inputStream != null;
            try (var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                var sql = reader.lines().collect(Collectors.joining("\n"));

                try (var statement = connection.createStatement()) {
                    connection.setAutoCommit(false);
                    statement.execute(sql);
                    connection.commit();
                    connection.setAutoCommit(true);
                }
            }
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("jte", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
