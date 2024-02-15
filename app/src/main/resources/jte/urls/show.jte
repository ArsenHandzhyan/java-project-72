@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@import java.time.format.DateTimeFormatter


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
        <div class="site-info">
            <h2>Сайт: ${page.getUrl().getName()}</h2>
            <table class="table">
                <tbody>
                <tr>
                    <td>ID</td>
                    <td>${page.getUrl().getId()}</td>
                </tr>
                <tr>
                    <td>Имя</td>
                    <td>${page.getUrl().getName()}</td>
                </tr>
                <tr>
                    <td>Дата создания</td>
                    <td>${page.getUrl().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                </tr>
                </tbody>
            </table>
        </div>
        <h2>Проверки</h2>
        <p><a href="${NamedRoutes.checksUrlPath(page.getUrl().getId())}">Запустить проверку</a></p>
        <table class="table">
            <thead>
            <tr>
                <th>ID</th>
                <th>Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th>Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            @if (page.getUrlChecks() != null)
                @for(var index = page.getUrlChecks().size() - 1; index >= 0; index--)
                    <tr>
                        <td>${index + 1}</td>
                        <td>${page.getUrlChecks().get(index).getStatusCode()}</td>
                        <td>${page.getUrlChecks().get(index).getTitle()}</td>
                        <td>${page.getUrlChecks().get(index).getH1()}</td>
                        <td>${page.getUrlChecks().get(index).getDescription()}</td>
                        <td>${page.getUrlChecks().get(index).getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}</td>
                    </tr>
                @endfor
            @endif
            </tbody>
        </table>
    @endif
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.homePath()}">Главная</a></p>
    <p><a href="${NamedRoutes.urlsPath()}">Назад</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
