import hexlet.code.App;
import io.javalin.Javalin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppTest {

    @BeforeEach
    public void setUp() throws Exception {
        Javalin app = App.getApp();
        app.start(0); // Запускаем сервер на случайном порту
    }

    @Test
    void testH2Connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:project");
        Assertions.assertNotNull(connection);
        connection.close();
    }

    @Test
    void testPostgresConnection() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://dpg-cmuok6acn0vc73akdjfg-a.oregon-postgres"
                + ".render.com/new_postgresql_for_javalin?"
                + "user=new_postgresql_for_javalin_user&"
                + "password=GvGwspqIZhAYD3HDJjbP9QP51RSh5yf9";
        Connection connection = DriverManager.getConnection(jdbcUrl);
        Assertions.assertNotNull(connection);
        connection.close();
    }

//    @Test
//    public void testHomeRoute() {
//        // Ожидаемый код ответа для маршрута "/"
//        int expectedStatusCode = 200;
//
//        // Фактический код ответа
//        int actualStatusCode = Unirest.get("http://localhost:8080" + "/").asString().getStatus();
//
//        // Проверка
//        assertEquals(expectedStatusCode, actualStatusCode);
//    }
}
