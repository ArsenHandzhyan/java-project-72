package hexlet.code.util;

/**
 * NamedRoutes.
 */
public class NamedRoutes {
    public static String homePath() {
        return "/";
    }

    public static String urlsPath() {
        return "/urls";
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String buildUrlPath() {
        return "/urls";
    }

    public static String checksUrlPath(Long id) {
        return checksUrlPath(String.valueOf(id));
    }

    public static String checksUrlPath(String id) {
        return "/urls/" + id + "/checks";
    }
}
