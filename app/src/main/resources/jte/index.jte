@import hexlet.code.dto.MainPage
@import hexlet.code.util.NamedRoutes

@param MainPage page

@template.layout.page(
content = @`
    <body>
    <h1>Добавить адрес</h1>
    <form action="${NamedRoutes.homePath()}" method="POST">
        <label for="url">Введите адрес:</label>
        <input type="text" id="url" name="url" required>
        <button type="submit">Добавить</button>
    </form>

    <!-- Отображение flash-сообщений -->
    <div>
        <!-- Сообщение об успешном добавлении адреса -->
        @if (page.getCtx()..attribute("success") != null)
            <div class="footer">
                ${ctx.attribute("success")}
            </div>
        @endif
<%--        <% if (ctx.attribute("success") != null) { %>--%>
<%--        <div class="alert alert-success" role="alert">--%>
<%--            <%= ctx.attribute("success") %>--%>
<%--        </div>--%>
<%--        <% } %>--%>

        <!-- Сообщение об ошибке (некорректный URL или уже существующий адрес) -->
        @if (ctx.attribute("error") != null)
            <div class="footer">
                ${ctx.attribute("error")}
            </div>
        @endif
<%--        <% if (ctx.attribute("error") != null) { %>--%>
<%--        <div class="alert alert-danger" role="alert">--%>
<%--            <%= ctx.attribute("error") %>--%>
<%--        </div>--%>
<%--        <% } %>--%>
    </div>
    </body>
`,
footer = @`
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
