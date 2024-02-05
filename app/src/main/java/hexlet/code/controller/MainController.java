package hexlet.code.controller;

import io.javalin.http.Context;
import hexlet.code.dto.MainPage;

import java.util.Collections;

public class MainController {
    public static void index(Context ctx) {
        var page = new MainPage(ctx.sessionAttribute("currentUser"));
        ctx.render("index.jte", Collections.singletonMap("page", page));

    }
}
