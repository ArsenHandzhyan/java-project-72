@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We"
              crossorigin="anonymous">
        <title>Hello Hexlet!</title>
    </head>
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
    <p><a href="${NamedRoutes.homePath()}">Главная</a></p>
    <p><a href="${NamedRoutes.homePath()}">назад</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
