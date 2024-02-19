package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import org.apache.commons.validator.routines.DomainValidator;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MainController {
    public static void index(Context ctx) {
        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flashType");
        var page = new MainPage(ctx.sessionAttribute("currentUser"));
        page.setFlash((String) flash);
        page.setFlashType((String) flashType);
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");
        try {
            assert inputUrl != null;
            URI uri = new URI(inputUrl);
            if (uri.getHost() == null || !DomainValidator.getInstance().isValid(uri.getHost())) {
                handleInvalidUrl(ctx, "Некорректный URL: отсутствует действительный хост");
                return;
            }
            if (!isValidUrl(uri)) {
                handleInvalidUrl(ctx, "Некорректный URL");
                return;
            }
            String domainWithProtocolAndPort = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                domainWithProtocolAndPort += ":" + uri.getPort();
            }
            Optional<Url> existingUrl = UrlsRepository.findUrlByUrl(domainWithProtocolAndPort);
            if (existingUrl.isPresent()) {
                handleExistingUrl(ctx);
                return;
            }
            Url url = new Url(domainWithProtocolAndPort, LocalDateTime.now());
            UrlsRepository.save(url);
            handleUrlAdded(ctx);
        } catch (Exception e) {
            handleInvalidUrl(ctx, "Некорректный URL: " + e.getMessage());
        }
    }

    private static boolean isValidUrl(URI uri) {
        return uri.getScheme() != null
                && (uri.getScheme().equalsIgnoreCase("http")
                || uri.getScheme().equalsIgnoreCase("https"));
    }

    private static void handleInvalidUrl(Context ctx, String message) {
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.homePath());
    }

    private static void handleExistingUrl(Context ctx) throws SQLException {
        ctx.sessionAttribute("flash", "Страница уже существует");
        ctx.sessionAttribute("flashType", determineFlashType(true));
        List<Url> urls = UrlsRepository.getEntities();
        renderUrlsPage(ctx, urls);
    }

    private static void handleUrlAdded(Context ctx) throws SQLException {
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flashType", determineFlashType(true));
        List<Url> urls = UrlsRepository.getEntities();
        renderUrlsPage(ctx, urls);
    }

    private static void renderUrlsPage(Context ctx, List<Url> urls) {
        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flashType");
        ctx.attribute("urls", urls);
        var page = new UrlsPage(urls);
        page.setFlash((String) flash);
        page.setFlashType((String) flashType);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }

    private static String determineFlashType(boolean isSuccess) {
        return isSuccess ? "alert-success" : "alert-danger";
    }
}
