package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.controller.MainController;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.get(NamedRoutes.homePath(), MainController::index);
//        app.get(NamedRoutes.usersPath(), UsersController::index);
//        app.get(NamedRoutes.buildUserPath(), UsersController::build);
//        app.get(NamedRoutes.userPath("{id}"), UsersController::show);
//        app.post(NamedRoutes.usersPath(), UsersController::create);
//        app.get(NamedRoutes.editUserPath("{id}"), UsersController::edit);
//        app.patch(NamedRoutes.userPath("{id}"), UsersController::update);
//        app.delete(NamedRoutes.userPath("{id}"), UsersController::destroy);
//
//        app.get(NamedRoutes.coursesPath(), CoursesController::index);
//        app.get(NamedRoutes.buildCoursePath(), CoursesController::build);
//        app.get(NamedRoutes.coursePath("{id}"), CoursesController::show);
//        app.post(NamedRoutes.coursesPath(), CoursesController::create);
//        app.get(NamedRoutes.editCoursePath("{id}"), CoursesController::edit);
//        app.post(NamedRoutes.coursePath("{id}"), CoursesController::update);
//        app.get(NamedRoutes.deleteCoursePath("{id}"), CoursesController::destroy);
//
//
//        // Отображение формы логина
//        app.get(NamedRoutes.buildSessionPath(), SessionsController::build);
//// Процесс логина
//        app.post(NamedRoutes.sessionsPath(), SessionsController::create);
//// Процесс выхода из аккаунта
//        app.delete(NamedRoutes.buildSessionPath(), SessionsController::destroy);
//
//        app.get(NamedRoutes.carsPath(), CarController::index);
//        app.get(NamedRoutes.buildCarPath(), CarController::build);
//        app.get(NamedRoutes.carPath("{id}"), CarController::show);
//        app.post(NamedRoutes.carsPath(), CarController::create);
//        app.get(NamedRoutes.editCarPath("{id}"), CarController::edit);
//        app.post(NamedRoutes.carPath("{id}"), CarController::update);
//        app.get(NamedRoutes.deleteCarPath("{id}"), CarController::destroy);


//        // Получаем значение переменной окружения PORT, если она установлена
//        String portStr = System.getenv("PORT");
//
//        // Проверяем, установлена ли переменная окружения PORT
//        // Если нет, используем значение по умолчанию (например, 7000)
//        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 8080;
        app.start(app.port());
    }

    public static Javalin getApp() throws SQLException, IOException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://dpg-cmuok6acn0vc73akdjfg-a.oregon-postgres.render.com/new_postgresql_for_javalin");
        hikariConfig.setUsername("new_postgresql_for_javalin_user");
        hikariConfig.setPassword("GvGwspqIZhAYD3HDJjbP9QP51RSh5yf9");

        try (var dataSource = new HikariDataSource(hikariConfig)) {
            // Получаем путь до файла в src/main/resources
            var url = App.class.getClassLoader().getResource("schema.sql");
            assert url != null;

            try (var inputStream = url.openStream();
                 var reader = new BufferedReader(new InputStreamReader(inputStream))) {
                var sql = reader.lines().collect(Collectors.joining("\n"));

                // Оставьте остальную часть кода без изменений
                try (var connection = dataSource.getConnection();
                     var statement = connection.createStatement()) {
                    statement.execute(sql);
                }
            }

            return Javalin.create();
        }
    }
}