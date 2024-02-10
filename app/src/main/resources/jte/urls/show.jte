@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

@template.layout.page(
content = @`
    <body>
    <h1>Просмотр URL</h1>
    <% if (ctx.attribute("url") != null) { %>
    <p>URL: <%= ctx.attribute("url").getUrl() %></p>
    <% } else { %>
    <p>URL не найден</p>
    <% } %>
    </body>
`,
footer = @`
<p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
