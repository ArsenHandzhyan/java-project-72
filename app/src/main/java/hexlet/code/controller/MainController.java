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
                ctx.sessionAttribute("flash", "Некорректный URL: схема или хост отсутствуют");
                ctx.redirect(NamedRoutes.homePath());
                return;
            }

            String domainWithProtocolAndPort = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                domainWithProtocolAndPort += ":" + uri.getPort();
            }

            Url existingUrl = UrlsRepository.findUrlByUrl(domainWithProtocolAndPort).orElse(null);
            if (existingUrl != null) {
                ctx.sessionAttribute("flash", "Страница уже существует");
                ctx.redirect(NamedRoutes.urlsPath());
                return;
            }

            Url url = new Url(domainWithProtocolAndPort, LocalDateTime.now());
            UrlsRepository.save(url);
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            var flash = ctx.consumeSessionAttribute("flash");
            List<Url> urls = UrlsRepository.getEntities();
            List<UrlCheck> urlChecks = new ArrayList<>();
            for (Url url1 : urls) {
                List<UrlCheck> checksForUrl = UrlCheckRepository.findByUrlId(url1.getId());
                urlChecks.addAll(checksForUrl);
            }
            ctx.attribute("urls", urls);
            var page = new UrlsPage(urls, urlChecks);
            page.setFlash((String) flash);
            ctx.render("urls/index.jte", Collections.singletonMap("page", page));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL: " + e.getMessage());
            ctx.redirect(NamedRoutes.homePath());
        }
    }
}
