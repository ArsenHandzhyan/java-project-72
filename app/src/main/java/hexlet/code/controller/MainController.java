package hexlet.code.controller;

import hexlet.code.dto.MainPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainController {
    public static void index(Context ctx) {
        var flash = ctx.consumeSessionAttribute("flash");
        var page = new MainPage(ctx.sessionAttribute("currentUser"));
        page.setFlash((String) flash);
        ctx.render("index.jte", Collections.singletonMap("page", page));
    }

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");

        try {
            assert inputUrl != null;
            URI uri = new URI(inputUrl);

            if (uri.getScheme() == null || uri.getHost() == null) {
                handleInvalidUrl(ctx, "Некорректный URL: схема или хост отсутствуют");
                return;
            }

            String domainWithProtocolAndPort = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                domainWithProtocolAndPort += ":" + uri.getPort();
            }

            if (UrlsRepository.findUrlByUrl(domainWithProtocolAndPort).isPresent()) {
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

    private static void handleInvalidUrl(Context ctx, String message) {
        ctx.sessionAttribute("flash", message);
        ctx.redirect(NamedRoutes.homePath());
    }

    private static void handleExistingUrl(Context ctx) {
        ctx.sessionAttribute("flash", "Страница уже существует");
        ctx.redirect(NamedRoutes.urlsPath());
    }

    private static void handleUrlAdded(Context ctx) throws SQLException {
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        List<Url> urls = UrlsRepository.getEntities();
        List<UrlCheck> urlChecks = new ArrayList<>();
        for (Url url : urls) {
            List<UrlCheck> checksForUrl = UrlCheckRepository.findByUrlId(url.getId());
            urlChecks.addAll(checksForUrl);
        }
        renderUrlsPage(ctx, urls, urlChecks);
    }

    private static void renderUrlsPage(Context ctx, List<Url> urls, List<UrlCheck> urlChecks) {
        var flash = ctx.consumeSessionAttribute("flash");
        ctx.attribute("urls", urls);
        var page = new UrlsPage(urls, urlChecks);
        page.setFlash((String) flash);
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }
}
