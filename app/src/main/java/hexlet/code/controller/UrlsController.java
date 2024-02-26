package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.apache.commons.validator.routines.DomainValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UrlsController {
    public static void showAllUrls(Context ctx) {
        try {
            List<Url> urls = UrlsRepository.getEntities();
            ctx.attribute("urls", urls);
            var page = new UrlsPage(urls);
            ctx.render("urls/index.jte", Collections.singletonMap("page", page));
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении списка URL");
            ctx.sessionAttribute("flashType", determineFlashType(false));
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void showUrlById(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(id);
            ctx.status(200);
            if (url != null) {
                ctx.attribute("url", url);
                var page = new UrlPage(url, urlChecks);
                var flash = ctx.consumeSessionAttribute("flash");
                var flashType = ctx.consumeSessionAttribute("flashType");
                page.setFlash((String) flash);
                page.setFlashType((String) flashType);
                ctx.render("urls/show.jte", Collections.singletonMap("page", page));
            } else {
                ctx.sessionAttribute("flash", "URL с указанным ID не найден");
                ctx.sessionAttribute("flashType", determineFlashType(false));
                List<Url> urls = UrlsRepository.getEntities();
                var page = new UrlsPage(urls);
                var flash = ctx.consumeSessionAttribute("flash");
                var flashType = ctx.consumeSessionAttribute("flashType");
                page.setFlash((String) flash);
                page.setFlashType((String) flashType);
                ctx.render("urls/index.jte", Collections.singletonMap("page", page));
            }
        } catch (SQLException e) {
            id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlsRepository.find(id).orElse(null);
            List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(id);
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении URL по ID");
            ctx.sessionAttribute("flashType", determineFlashType(false));
            var page = new UrlPage(url, urlChecks);
            var flash = ctx.consumeSessionAttribute("flash");
            var flashType = ctx.consumeSessionAttribute("flashType");
            page.setFlash((String) flash);
            page.setFlashType((String) flashType);
            ctx.render("urls/show.jte", Collections.singletonMap("page", page));
        }
    }

    public static void checkUrl(Context ctx) {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            if (url == null) {
                handleUrlNotFound(ctx);
                return;
            }
            String inputUrl = url.getName();
            URI uri = new URI(inputUrl);
            if (uri.getHost() == null || !DomainValidator.getInstance().isValid(uri.getHost())) {
                handleInvalidUrl(ctx, "Некорректный URL: отсутствует действительный хост", id);
                return;
            }
            if (!isValidUrl(uri)) {
                handleInvalidUrl(ctx, "Некорректный URL", id);
                return;
            }

            HttpResponse<String> response = sendHttpRequest(inputUrl);
            if (response == null) {
                handleFailedHttpRequest(ctx, url);
                return;
            }

            handleSuccessfulHttpRequest(ctx, url, response);
        } catch (Exception e) {
            handleInvalidUrl(ctx, "Некорректный URL: " + e.getMessage(), id);
        }
    }

    private static void handleSuccessfulHttpRequest(Context ctx, Url url, HttpResponse<String> response)
            throws SQLException {
        saveUrlCheck(ctx, url, response);
    }

    private static void saveUrlCheck(Context ctx, Url url, HttpResponse<String> response)
            throws SQLException {
        Document document = Jsoup.parse(response.getBody());
        String title = "";
        String h1 = "";
        String description = "";
        int statusCode = response.getStatus();

        if (statusCode == 200) {
            title = document.title();
            h1 = getFirstElementText(document.select("h1"));
            description = document.select("meta[name=description]").attr("content");
        }
        long urlCheckId = UrlCheckRepository.getNextIdForUrl(url.getId());
        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setUrl(url);
        urlCheck.setId(urlCheckId);
        urlCheck.setUrlId(url.getId());
        urlCheck.setStatusCode(statusCode);
        urlCheck.setCreatedAt(LocalDateTime.now());
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);
        UrlCheckRepository.save(urlCheck);

        ctx.status(200);
        ctx.sessionAttribute("flash", "URL успешно проверен");
        ctx.sessionAttribute("flashType", determineFlashType(true));
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
    }

    private static HttpResponse<String> sendHttpRequest(String url) {
        try {
            return Unirest.get(url).asString();
        } catch (UnirestException e) {
            return null;
        }
    }
    private static void handleUrlNotFound(Context ctx) {
        ctx.status(200);
        ctx.sessionAttribute("flash", "URL-адрес с указанным идентификатором не найден");
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.urlsPath());
    }

    private static void handleFailedHttpRequest(Context ctx, Url url) {
        ctx.status(500);
        ctx.sessionAttribute("flash", "Не удалось выполнить HTTP-запрос");
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
    }

    private static String getFirstElementText(Elements elements) {
        return elements.first() != null ? Objects.requireNonNull(elements.first()).text() : "";
    }
    private static boolean isValidUrl(URI uri) {
        return uri.getScheme() != null
                && (uri.getScheme().equalsIgnoreCase("http")
                || uri.getScheme().equalsIgnoreCase("https"));
    }

    private static void handleInvalidUrl(Context ctx, String message, Long id) {
        ctx.status(200);
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.urlPath(id));
    }

    private static String determineFlashType(boolean isSuccess) {
        return isSuccess ? "alert-success" : "alert-danger";
    }
}
