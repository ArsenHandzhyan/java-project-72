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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UrlsController {
    public static void showAllUrls(Context ctx) {
        try {
            List<Url> urls = UrlsRepository.getEntities();
            List<UrlCheck> urlChecks = new ArrayList<>();
            for (Url url : urls) {
                List<UrlCheck> checksForUrl = UrlCheckRepository.findByUrlId(url.getId());
                urlChecks.addAll(checksForUrl);
            }
            ctx.attribute("urls", urls);
            var page = new UrlsPage(urls, urlChecks);
            ctx.render("urls/index.jte", Collections.singletonMap("page", page));
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении списка URL");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void showUrlById(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(id);
            if (url != null) {
                ctx.attribute("url", url);
                var page = new UrlPage(url, urlChecks);
                var flash = ctx.consumeSessionAttribute("flash");
                page.setFlash((String) flash);
                ctx.render("urls/show.jte", Collections.singletonMap("page", page));
            } else {
                ctx.status(404);
                ctx.sessionAttribute("flash", "URL с указанным ID не найден");
                List<Url> urls = UrlsRepository.getEntities();
                var page = new UrlsPage(urls, urlChecks);
                var flash = ctx.consumeSessionAttribute("flash");
                page.setFlash((String) flash);
                ctx.render("urls/index.jte", Collections.singletonMap("page", page));
            }
        } catch (SQLException e) {
            id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlsRepository.find(id).orElse(null);
            List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(id);
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении URL по ID");
            var page = new UrlPage(url, urlChecks);
            var flash = ctx.consumeSessionAttribute("flash");
            page.setFlash((String) flash);
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

            HttpResponse<String> response = sendHttpRequest(url);
            if (response == null) {
                handleFailedHttpRequest(ctx);
                return;
            }

            handleSuccessfulHttpRequest(ctx, url, response);
        } catch (SQLException e) {
            handleSQLException(ctx);
        }
    }

    private static void handleUrlNotFound(Context ctx) {
        ctx.status(404);
        ctx.sessionAttribute("flash", "URL-адрес с указанным идентификатором не найден");
        ctx.redirect(NamedRoutes.urlsPath());
    }

    private static HttpResponse<String> sendHttpRequest(Url url) {
        try {
            return Unirest.post(url.getName()).asString();
        } catch (UnirestException e) {
            return null;
        }
    }

    private static void handleFailedHttpRequest(Context ctx) {
        ctx.status(500);
        ctx.sessionAttribute("flash", "Не удалось отправить HTTP-запрос");
    }

    private static void handleSuccessfulHttpRequest(Context ctx, Url url, HttpResponse<String> response)
            throws SQLException {
        Document document = Jsoup.parse(response.getBody());
        String title = "";
        String h1 = "";
        String description = "";

        if (response.getStatus() == 200) {
            title = document.title();
            h1 = getFirstElementText(document.select("h1"));
            description = document.select("meta[name=description]").attr("content");
        }

        saveUrlCheck(ctx, url, response.getStatus(), title, h1, description);
    }

    private static String getFirstElementText(Elements elements) {
        return elements.first() != null ? Objects.requireNonNull(elements.first()).text() : "";
    }

    private static void saveUrlCheck(Context ctx, Url url, int statusCode, String title, String h1, String description)
            throws SQLException {
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

        ctx.status(201);
        ctx.sessionAttribute("flash", "URL успешно проверен");
        ctx.redirect(NamedRoutes.urlPath(url.getId()));
    }

    private static void handleSQLException(Context ctx) {
        ctx.status(500);
        ctx.sessionAttribute("flash", "Произошла ошибка при проверке URL-адреса.");
    }
}
