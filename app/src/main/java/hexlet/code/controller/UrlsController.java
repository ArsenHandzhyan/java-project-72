package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UrlsController {
    public static void showAllUrls(Context ctx) {
        try {
            List<Url> urls = UrlsRepository.getEntities();
            ctx.attribute("urls", urls);
            var page = new UrlsPage(urls, null, null);
            ctx.render("urls/index.jte", Collections.singletonMap("page", page));
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении списка URL");
            ctx.sessionAttribute("flashType", determineFlashType(false));
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void showUrlById(Context ctx) {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            if (url == null) {
                ctx.status(HttpStatus.NOT_FOUND);
                ctx.sessionAttribute("flash", "URL с указанным ID не найден");
                ctx.sessionAttribute("flashType", determineFlashType(false));
                List<Url> urls = UrlsRepository.getEntities();
                var page = new UrlsPage(urls, null, null);
                var flash = ctx.consumeSessionAttribute("flash");
                var flashType = ctx.consumeSessionAttribute("flashType");
                page.setFlash((String) flash);
                page.setFlashType((String) flashType);
                ctx.render("urls/index.jte", Collections.singletonMap("page", page));
                return;
            }
            List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(id);
            ctx.status(200);
            getPage(ctx, url, urlChecks);
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении URL по ID");
            ctx.sessionAttribute("flashType", determineFlashType(false));
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    private static void getPage(Context ctx, Url url, List<UrlCheck> urlChecks) {
        var page = new UrlPage(url, urlChecks);
        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flashType");
        page.setFlash((String) flash);
        page.setFlashType((String) flashType);
        ctx.render("urls/show.jte", Collections.singletonMap("page", page));
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
            if (!isValidUrl(inputUrl)) {
                handleInvalidUrl(ctx, "Некорректный URL", id);
                return;
            }
            HttpResponse<String> response = sendHttpRequest(inputUrl).orElse(handleFailedHttpRequest(ctx, url));
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
        UrlCheck urlCheck = new UrlCheck();
        urlCheck.setUrl(url);
        urlCheck.setUrlId(url.getId());
        urlCheck.setStatusCode(statusCode);
        urlCheck.setTitle(title);
        urlCheck.setH1(h1);
        urlCheck.setDescription(description);
        UrlCheckRepository.save(urlCheck);
        ctx.status(200);
        ctx.sessionAttribute("flash", "URL успешно проверен");
        ctx.sessionAttribute("flashType", determineFlashType(true));
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
    }

    public static Optional<HttpResponse<String>> sendHttpRequest(String url) {
        try {
            HttpResponse<String> response = Unirest.get(url).asString();
            return Optional.of(response);
        } catch (UnirestException e) {
            System.err.println("Ошибка при выполнении HTTP-запроса: " + e.getMessage());
            return Optional.empty();
        }
    }

    private static String getFirstElementText(Elements elements) {
        return elements.first() != null ? Objects.requireNonNull(elements.first()).text() : "";
    }

    private static boolean isValidUrl(String url) {
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null && uri.getHost() != null
                    && (uri.getScheme().equalsIgnoreCase("http")
                    || uri.getScheme().equalsIgnoreCase("https"));
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private static void handleUrlNotFound(Context ctx) {
        ctx.status(HttpStatus.NOT_FOUND);
        ctx.sessionAttribute("flash", "URL-адрес с указанным идентификатором не найден");
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.urlsPath());
    }

    private static HttpResponse<String> handleFailedHttpRequest(Context ctx, Url url) {
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR);
        ctx.sessionAttribute("flash", "Не удалось выполнить HTTP-запрос");
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
        return null;
    }

    private static void handleInvalidUrl(Context ctx, String message, Long id) {
        ctx.status(HttpStatus.BAD_REQUEST);
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", determineFlashType(false));
        ctx.redirect(NamedRoutes.urlPath(id));
    }

    private static String determineFlashType(boolean isSuccess) {
        return isSuccess ? "alert-success" : "alert-danger";
    }
}
