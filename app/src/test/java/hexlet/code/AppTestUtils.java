package hexlet.code;

public class AppTestUtils {
    public static void waitForApp() {
        try {
            // Подождать некоторое время перед выполнением следующих запросов
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
