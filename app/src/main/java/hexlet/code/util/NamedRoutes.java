package hexlet.code.util;

/**
 * NamedRoutes.
 */
public class NamedRoutes {
    public static String homePath() {
        return "/";
    }

    public static String urlsPath() {
        return "/templates/jte/urls";
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlPath(String id) {
        return "/templates/jte/urls/" + id;
    }

    public static String buildUrlPath() {
        return "/templates/jte/urls/build";
    }

    public static String editUrlPath(Long id) {
        return editUrlPath(String.valueOf(id));
    }

    public static String editUrlPath(String id) {
        return "/templates/jte/urls/" + id + "/edit";
    }
}
