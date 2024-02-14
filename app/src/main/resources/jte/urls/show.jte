@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes

@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.0/dist/css/bootstrap.min.css"
              rel="stylesheet"
              integrity="sha384-KyZXEAg3QhqLMpG8r+8fhAXLRk2vvoC2f3B09zVXn8CA5QIVfZOJ3BCsw2P0p/We"
              crossorigin="anonymous">
        <title>Hello Hexlet!</title>
    </head>
    <body>
    <h1>Просмотр URL</h1>
    @if (page != null && page.getUrl() != null)
        <div>${page.getUrl().toString()}</div>
        <p><a href="${NamedRoutes.checksUrlPath(page.getUrl().getId())}">Запустить проверку</a></p>
    @endif
    @if (page != null && page.getUrlChecks() != null)
        @for(var index = 0; index < page.getUrlChecks().size(); index++)
            <div>Проверка №${index + 1}</div>
            <div>Id: ${page.getUrlChecks().get(index).getId()}</div>
            <div>Title: ${page.getUrlChecks().get(index).getTitle()}</div>
            <div>H1: ${page.getUrlChecks().get(index).getH1()}</div>
            <div>Description: ${page.getUrlChecks().get(index).getDescription()}</div>
            <div>Connection: ${page.getUrlChecks().get(index).toString()}</div>
            <div>Status: ${page.getUrlChecks().get(index).getStatusCode()}</div>
        @endfor
    @endif
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.homePath()}">Главная</a></p>
    <p><a href="${NamedRoutes.urlsPath()}">Назад</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
