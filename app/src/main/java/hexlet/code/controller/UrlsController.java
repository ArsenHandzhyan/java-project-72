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

    public static void checkUrl(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        List<UrlCheck> urlChecks = UrlCheckRepository.findByUrlId(id);
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            if (url != null) {
                HttpResponse<String> response;
                try {
                    // Отправляем запрос и получаем тело ответа в виде строки
                    response = Unirest.post(url.getName()).asString();

                    // Получаем тело ответа в виде строки
                    String responseBody = response.getBody();

                    // Преобразуем строку в объект Document (например, если это HTML)
                    Document document = Jsoup.parse(responseBody);

                    // Получаем необходимые данные из документа

                    String title = "title not found";
                    String h1 = "h1 not found";
                    String description = "description not found";

                    if (response.getStatus() == 200) { // Проверяем успешность запроса
                        title = document.title();
                        h1 = Objects.requireNonNull(document.select("h1").first()).text();
                        description = document.select("meta[name=description]").attr("content");
                    }

                    long urlCheckId = UrlCheckRepository.getNextIdForUrl(url.getId());
                    UrlCheck urlCheck = new UrlCheck();
                    urlCheck.setUrl(url);
                    urlCheck.setId(urlCheckId);
                    urlCheck.setUrlId(url.getId());
                    urlCheck.setStatusCode(response.getStatus());
                    urlCheck.setCreatedAt(LocalDateTime.now());
                    urlCheck.setTitle(title);
                    urlCheck.setH1(h1);
                    urlCheck.setDescription(description);

                    UrlCheckRepository.save(urlCheck);

                    ctx.status(201);
                    ctx.sessionAttribute("flash", "Проверка URL произошла успешно");
                    var flash = ctx.consumeSessionAttribute("flash");
                    var page = new UrlPage(url, urlChecks);
                    page.setFlash((String) flash);
                    ctx.render("urls/show.jte", Collections.singletonMap("page", page));
                } catch (UnirestException e) {
                    ctx.status(500);
                    ctx.sessionAttribute("flash", "Failed to send HTTP request");
                    var flash = ctx.consumeSessionAttribute("flash");
                    var page = new UrlPage(url, urlChecks);
                    page.setFlash((String) flash);
                    ctx.render("urls/show.jte", Collections.singletonMap("page", page));
                }
            } else {
                ctx.status(404);
                ctx.sessionAttribute("flash", "URL с указанным ID не найден");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (SQLException e) {
            Url url = UrlsRepository.find(id).orElse(null);
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при выполнении проверки URL");
            var flash = ctx.consumeSessionAttribute("flash");
            var page = new UrlPage(url, urlChecks);
            page.setFlash((String) flash);
            ctx.render("urls/show.jte", Collections.singletonMap("page", page));
        }
    }
}
