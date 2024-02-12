@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <body>
    <h1>Список добавленных URL</h1>
    <ul>
        @if (page != null && !page.getUrls().isEmpty())
            @for(var url : page.getUrls())
                <div>
                    <p><a href="${NamedRoutes.urlsPath()}/${url.getId()}">
                            <div>Name: ${url.getName()} CreatedAt: ${url.getCreatedAt().toString()}</div>
                        </a></p>
                </div>
            @endfor
        @else
            <p>Список пустой!</p>
        @endif
    </ul>
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.homePath()}">назад</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
