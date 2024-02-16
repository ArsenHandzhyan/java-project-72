package hexlet.code.repository;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlHelper {
    public static String normalizeUrl(String inputUrl) {
        try {
            URI uri = new URI(inputUrl);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https"))) {
                return null; // Некорректная схема URL
            }
            if (host == null) {
                return null; // Отсутствует хост в URL
            }
            int port = uri.getPort();
            if (port == -1) {
                return scheme + "://" + host;
            } else {
                return scheme + "://" + host + ":" + port;
            }
        } catch (URISyntaxException e) {
            return null; // Некорректный URL
        }
    }
}
