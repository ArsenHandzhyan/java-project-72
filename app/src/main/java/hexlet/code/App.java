package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.controller.MainController;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;

public class App {

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.get(NamedRoutes.homePath(), MainController::index);
//        app.get(NamedRoutes.urlsPath(), UrlsController::index);
//        app.get(NamedRoutes.buildUrlPath(), UrlsController::build);
//        app.get(NamedRoutes.urlPath("{id}"), UrlsController::show);
//        app.post(NamedRoutes.urlsPath(), UrlsController::create);
//        app.get(NamedRoutes.editUrlPath("{id}"), UrlsController::edit);
//        app.patch(NamedRoutes.urlPath("{id}"), UrlsController::update);
//        app.delete(NamedRoutes.urlPath("{id}"), UrlsController::destroy);

//        // Отображение формы логина
//        app.get(NamedRoutes.buildSessionPath(), SessionsController::build);
//// Процесс логина
//        app.post(NamedRoutes.sessionsPath(), SessionsController::create);
//// Процесс выхода из аккаунта
//        app.delete(NamedRoutes.buildSessionPath(), SessionsController::destroy);


//        // Получаем значение переменной окружения PORT, если она установлена
//        String portStr = System.getenv("PORT");
//
//        // Проверяем, установлена ли переменная окружения PORT
//        // Если нет, используем значение по умолчанию (например, 8080)
//        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 8080;
        app.start(app.port());
    }

    public static Javalin getApp() throws SQLException, IOException {
        JavalinJte.init(createTemplateEngine());
        String jdbcUrl = System.getenv("jdbc:postgresql:"
                + "//dpg-cmuok6acn0vc73akdjfg-a.oregon-postgres"
                + ".render.com/new_postgresql_for_javalin");
        HikariConfig hikariConfig = new HikariConfig();

        if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
            hikariConfig.setJdbcUrl(jdbcUrl);
        } else {
            hikariConfig.setJdbcUrl("jdbc:h2:mem:project");
        }

        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            hikariConfig.setUsername("new_postgresql_for_javalin_user");
            hikariConfig.setPassword("GvGwspqIZhAYD3HDJjbP9QP51RSh5yf9");
        }

        try (var dataSource = new HikariDataSource(hikariConfig)) {
            var url = App.class.getClassLoader().getResource("schema.sql");
            assert url != null;

            try (var inputStream = url.openStream();
                 var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                var sql = reader.lines().collect(Collectors.joining("\n"));

                try (var connection = dataSource.getConnection();
                     var statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }

            return Javalin.create();
        }
    }

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates.jte", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
