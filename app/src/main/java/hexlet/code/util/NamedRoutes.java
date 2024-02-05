package hexlet.code.util;

/**
 * NamedRoutes.
 */
public class NamedRoutes {
    public static String homePath() {
        return "/";
    }

    public static String buildSessionPath() {
        return "/sessions/build";
    }

    public static String sessionsPath() {
        return "/sessions";
    }

    public static String deleteSessionsPath() {
        return "/sessions";
    }

    public static String buildCarPath() {
        return "/cars/build";
    }

    public static String editCarPath(Long id) {
        return editCarPath(String.valueOf(id));
    }

    public static String editCarPath(String id) {
        return "/cars/" + id + "/edit";
    }

    public static String carsPath() {
        return "/cars";
    }

    public static String deleteCarPath(Long id) {
        return deleteCarPath(String.valueOf(id));
    }

    public static String deleteCarPath(String id) {
        return "/cars/" + id + "/delete";
    }

    public static String carPath(Long id) {
        return carPath(String.valueOf(id));
    }

    public static String carPath(String id) {
        return "/cars/" + id;
    }

    public static String usersPath() {
        return "/users";
    }

    public static String userPath(Long id) {
        return userPath(String.valueOf(id));
    }

    public static String userPath(String id) {
        return "/users/" + id;
    }

    public static String buildUserPath() {
        return "/users/build";
    }

    public static String editUserPath(Long id) {
        return editUserPath(String.valueOf(id));
    }

    public static String editUserPath(String id) {
        return "/users/" + id + "/edit";
    }

    public static String buildCoursePath() {
        return "/courses/build";
    }

    public static String coursesPath() {
        return "/courses";
    }

    public static String coursePath(Long id) {
        return coursePath(String.valueOf(id));
    }

    public static String deleteCoursePath(Long id) {
        return deleteCoursePath(String.valueOf(id));
    }

    public static String deleteCoursePath(String id) {
        return "/courses/" + id + "/delete";
    }

    public static String editCoursePath(Long id) {
        return editCoursePath(String.valueOf(id));
    }

    public static String editCoursePath(String id) {
        return "/courses/" + id + "/edit";
    }

    public static String coursePath(String id) {
        return "/courses/" + id;
    }

    public static String postsPath() {
        return "/posts";
    }
}