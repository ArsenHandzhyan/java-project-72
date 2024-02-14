@import hexlet.code.dto.UrlPage
@import hexlet.code.util.NamedRoutes
@import java.time.LocalDateTime

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
                    <td>${page.getUrl().getCreatedAt().toString()}</td> <!-- Assuming a method getCreatedAt() exists -->
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
                @for(var check : page.getUrlChecks())
                    <tr>
                        <td>${check.getId()}</td>
                        <td>${check.getStatusCode()}</td>
                        <td>${check.getTitle()}</td>
                        <td>${check.getH1()}</td>
                        <td>${check.getDescription()}</td>
                        <td>${LocalDateTime.now().toString()}</td>
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
