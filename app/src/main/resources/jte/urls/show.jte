@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes

@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <body>
    <h1>Просмотр URL</h1>
    @if (page.getUrl() != null)
        <div>${page.getUrl().toString()}</div>
    @endif
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.urlsPath()}">назад</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
