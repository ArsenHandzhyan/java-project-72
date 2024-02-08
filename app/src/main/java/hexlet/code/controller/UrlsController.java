package hexlet.code.controller;

import hexlet.code.dto.BuildUrlPage;
import hexlet.code.dto.UrlPage;
import hexlet.code.dto.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.validation.ValidationException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;

public class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var flash = ctx.consumeSessionAttribute("flash");
        var nameSearch = ctx.formParam("name");
        var descriptionSearch = ctx.formParam("description");

        var url = UrlsRepository.searchEntities(nameSearch, descriptionSearch);
        var page = new UrlsPage(url);
        page.setFlash((String) flash);
        ctx.render("templates/jte/urls/index.jte", Collections.singletonMap("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var flash = ctx.consumeSessionAttribute("flash");
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Курс с id = " + id + " не существует"));
        var page = new UrlPage(url);
        page.setFlash(String.valueOf(flash));
        ctx.render("templates/jte/urls/show.jte", Collections.singletonMap("page", page));
    }

    public static void build(Context ctx) {
        ctx.render("templates/jte/urls/build.jte");
    }

    public static void create(Context ctx) throws SQLException {
        try {
            var name = ctx.formParam("name");
            var createdAt = ctx.formParamAsClass("createdAt", LocalDateTime.class);
            var url = new Url(name, LocalDateTime.parse((CharSequence) createdAt));
            UrlsRepository.save(url);
            var flash = ctx.consumeSessionAttribute("flash");
            var urls = UrlsRepository.getEntities();
            var page = new UrlsPage(urls);
            page.setFlash(String.valueOf(flash));
            ctx.sessionAttribute("flash", "Курс успешно добавлен!");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (ValidationException e) {
            var id = ctx.pathParamAsClass("id", Long.class).get();
            var name = ctx.formParam("name");
            var createdAt = ctx.formParamAsClass("createdAt", LocalDateTime.class);
            var page = new BuildUrlPage(id, name, LocalDateTime.parse((CharSequence) createdAt), e.getErrors());
            ctx.render("templates/jte/urls/build.jte", Collections.singletonMap("page", page));
        }
    }

    public static void edit(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlsRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Курс с id = " + id + " не существует"));
        var page = new UrlPage(url);
        ctx.render("templates/jte/urls/edit.jte", Collections.singletonMap("page", page));
    }

    public static void update(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var name = ctx.formParam("name");
        var createdAt = ctx.formParam("createdAt");
        UrlsRepository.update(id, name, createdAt);
        ctx.sessionAttribute("flash", "Курс успешно отредактирован!");
        ctx.redirect(NamedRoutes.urlsPath());
    }

    public static void destroy(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        UrlsRepository.delete(id);

        ctx.sessionAttribute("flash", "Курс удален!");
        ctx.redirect(NamedRoutes.urlsPath());
    }
}
