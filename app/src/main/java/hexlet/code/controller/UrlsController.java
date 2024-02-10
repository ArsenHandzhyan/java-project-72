package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;

import java.sql.SQLException;

public class UrlsController {
    public static void showAllUrls(Context ctx) {
        try {
            ctx.attribute("urls", UrlsRepository.getEntities());
            ctx.render("urls/index.jte");
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("error", "Произошла ошибка при получении списка URL");
            ctx.redirect(NamedRoutes.homePath());
        }
    }

    public static void showUrlById(Context ctx) {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        try {
            Url url = UrlsRepository.find(id).orElse(null);
            if (url != null) {
                ctx.attribute("url", url);
                ctx.render("urls/show.jte");
            } else {
                ctx.status(404);
                ctx.sessionAttribute("error", "URL с указанным ID не найден");
                ctx.render("urls/show.jte");
                ctx.redirect(NamedRoutes.homePath());
            }
        } catch (SQLException e) {
            ctx.status(500);
            ctx.sessionAttribute("error", "Произошла ошибка при получении URL по ID");
            ctx.render("urls/show.jte");
            ctx.redirect(NamedRoutes.homePath());
        }
    }
}
