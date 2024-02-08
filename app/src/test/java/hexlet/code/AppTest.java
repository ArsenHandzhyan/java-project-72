package hexlet.code;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppTest {

    @Test
    void testH2Connection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:h2:mem:project");
        assertNotNull(connection);
        connection.close();
    }

    @Test
    void testPostgresConnection() throws SQLException {
        String jdbcUrl = "jdbc:postgresql://dpg-cmuok6acn0vc73akdjfg-a.oregon-postgres"
                + ".render.com/new_postgresql_for_javalin?"
                + "user=new_postgresql_for_javalin_user&"
                + "password=GvGwspqIZhAYD3HDJjbP9QP51RSh5yf9";
        Connection connection = DriverManager.getConnection(jdbcUrl);
        assertNotNull(connection);
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
