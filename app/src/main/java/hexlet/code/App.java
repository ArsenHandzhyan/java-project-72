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
        app.start(app.port());
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
        app.post(NamedRoutes.homePath(), MainController::addUrl);
        app.get(NamedRoutes.urlsPath(), UrlsController::showAllUrls);
        app.get(NamedRoutes.urlPath("{id}"), UrlsController::showUrlById);
        app.get(NamedRoutes.checksUrlPath("{id}"), UrlsController::checkUrl);
    }

    private static HikariDataSource initializeDataSource() throws SQLException, IOException {
        String jdbcUrl = System.getenv("jdbc:postgresql://"
                + "dpg-cmuok6acn0vc73akdjfg-a.oregon-postgres.render.com"
                + "/new_postgresql_for_javalin");
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            jdbcUrl = "jdbc:h2:mem:project";
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);

        if (jdbcUrl.startsWith("jdbc:postgresql://")) {
            hikariConfig.setUsername("new_postgresql_for_javalin_user");
            hikariConfig.setPassword("GvGwspqIZhAYD3HDJjbP9QP51RSh5yf9");
        }

        try {
            var dataSource = new HikariDataSource(hikariConfig);
            var url = App.class.getClassLoader().getResource("schema.sql");
            assert url != null;

            try (var inputStream = url.openStream();
                 var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                var sql = reader.lines().collect(Collectors.joining("\n"));

                try (var connection = dataSource.getConnection();
                     var statement = connection.createStatement()) {
                    connection.setAutoCommit(false); // Установка автокоммита вручную
                    statement.execute(sql);
                    connection.commit(); // Фиксация изменений
                    connection.setAutoCommit(true); // Восстановление автокоммита
                }
            }
            return dataSource;
        } catch (SQLException e) {
            LOGGER.error("An error occurred while initializing data source", e);
            throw e;
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("jte", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}