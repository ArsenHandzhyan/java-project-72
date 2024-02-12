package hexlet.code.controller;

import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
//            ctx.sessionAttribute("flash", "Proizoshla oshibka pri poluchenii spiska URL");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }

    public static void showUrlById(Context ctx) {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            if (url != null) {
                ctx.attribute("url", url);
                var page = new UrlPage(url);
                ctx.render("urls/show.jte", Collections.singletonMap("page", page));
            } else {
                ctx.status(404);
                ctx.sessionAttribute("flash", "URL с указанным ID не найден");
//                ctx.sessionAttribute("flash", "URL s ukazannim id ne naidena");
                ctx.redirect(NamedRoutes.urlsPath());
            }
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("flash", "Произошла ошибка при получении URL по ID");
//            ctx.sessionAttribute("flash", "Proizoshla oshibka pri poluchenii URL po id");
            ctx.redirect(NamedRoutes.urlsPath());
        }
    }
}
