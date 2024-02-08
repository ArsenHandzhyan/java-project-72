@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

@template.layout.page(
content = @`
    <body>
    <h1>Список добавленных URL</h1>
    <ul>
        <% for (var url : ctx.attribute("urls")) { %>
        <li><a href="${NamedRoutes.urlsPath()}/<%= url.getId() %>"><%= url.getUrl() %></a></li>
        <% } %>
    </ul>
    </body>
`,
footer = @`
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)