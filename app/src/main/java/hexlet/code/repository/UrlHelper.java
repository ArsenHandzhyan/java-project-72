package hexlet.code.repository;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlHelper {
    public static String normalizeUrl(String inputUrl) {
        try {
            URI uri = new URI(inputUrl);
            String scheme = getScheme(uri);
            String host = getHost(uri);
            if (scheme == null || host == null) {
                return null;
            }
            int port = uri.getPort();
            return formatNormalizedUrl(scheme, host, port);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    private static String getScheme(URI uri) {
        String scheme = uri.getScheme();
        return (scheme != null
                && (scheme.equalsIgnoreCase("http")
                || scheme.equalsIgnoreCase("https"))) ? scheme : null;
    }

    private static String getHost(URI uri) {
        return uri.getHost();
    }

    private static String formatNormalizedUrl(String scheme, String host, int port) {
        if (port == -1) {
            return scheme + "://" + host;
        } else {
            return scheme + "://" + host + ":" + port;
        }
    }
}
