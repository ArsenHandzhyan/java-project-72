package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.net.URI;
import java.time.LocalDateTime;

public class MainController {
    public static void index(Context ctx) {
        ctx.render("index.jte");
    }

    public static void addUrl(Context ctx) {
        String inputUrl = ctx.formParam("url");

        try {
            assert inputUrl != null;
            URI uri = new URI(inputUrl);
            String domainWithProtocolAndPort = uri.getScheme() + "://" + uri.getHost();
            if (uri.getPort() != -1) {
                domainWithProtocolAndPort += ":" + uri.getPort();
            }

            Url existingUrl = UrlsRepository.findUrlByUrl(domainWithProtocolAndPort).orElse(null);
            if (existingUrl != null) {
                ctx.sessionAttribute("error", "Страница уже существует");
                ctx.redirect(NamedRoutes.homePath());
                return;
            }

            Url url = new Url(domainWithProtocolAndPort, LocalDateTime.now());
            UrlsRepository.save(url);
            ctx.sessionAttribute("success", "Страница успешно добавлена");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (Exception e) {
            ctx.sessionAttribute("error", "Некорректный URL");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
}
