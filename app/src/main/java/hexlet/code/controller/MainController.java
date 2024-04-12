package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

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

            if (!isValidUrl(uri)) {
                ctx.status(HttpStatus.BAD_REQUEST);
                handleInvalidUrl(ctx, "Некорректный URL");
                return;
            }
            String domainWithProtocolAndPort = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                domainWithProtocolAndPort += ":" + uri.getPort();
            }
            Optional<Url> existingUrl = UrlsRepository.findUrlByUrl(domainWithProtocolAndPort);
            if (existingUrl.isPresent()) {
                ctx.status(HttpStatus.CONFLICT);
                handleExistingUrl(ctx);
                return;
            }
            Url url = new Url(domainWithProtocolAndPort, LocalDateTime.now());
            UrlsRepository.save(url);
            ctx.status(HttpStatus.CREATED);
            handleUrlAdded(ctx);
        } catch (Exception e) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
            handleInvalidUrl(ctx, "Некорректный URL: " + e.getMessage());
        }
    }

    private static boolean isValidUrl(URI uri) {
        return uri.getScheme() != null && uri.getHost() != null
                && (uri.getScheme().equalsIgnoreCase("http")
                || uri.getScheme().equalsIgnoreCase("https"));
    }

    private static void handleInvalidUrl(Context ctx, String message) {
        ctx.status(HttpStatus.BAD_REQUEST);
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.homePath());
    }

    private static void handleExistingUrl(Context ctx) throws SQLException {
        ctx.status(HttpStatus.CONFLICT);
        ctx.sessionAttribute("flash", "Страница уже существует");
        ctx.sessionAttribute("flashType", determineFlashType(true));
        List<Url> urls = UrlsRepository.getEntities();
        renderUrlsPage(ctx, urls);
    }

    private static void handleUrlAdded(Context ctx) throws SQLException {
        ctx.status(HttpStatus.OK);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flashType", determineFlashType(true));
        List<Url> urls = UrlsRepository.getEntities();
        renderUrlsPage(ctx, urls);
    }

    private static void renderUrlsPage(Context ctx, List<Url> urls) {
        ctx.status(HttpStatus.OK);
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
