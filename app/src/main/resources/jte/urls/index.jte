@import hexlet.code.dto.UrlsPage
@import hexlet.code.util.NamedRoutes

@param UrlsPage page

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
    <h1>Сайты</h1>
    <table class="table">
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя</th>
            <th>Последняя проверка</th>
            <th>Код ответа</th>
        </tr>
        </thead>
        <tbody>
        @if (page != null && !page.getUrls().isEmpty())
            @for(var urlPage : page.getUrls())
                <tr>
                    <td>${urlPage.getId()}</td>
                    <td><a href="${NamedRoutes.urlsPath()}/${urlPage.getId()}">${urlPage.getName()}</a></td>
                    <td>${String.valueOf(page.getLastCheckDate())}</td>
                    <td>${page.getStatusCode()}</td>
                </tr>
            @endfor
        @else
            <tr>
                <td colspan="4">Список пустой!</td>
            </tr>
        @endif
        </tbody>
    </table>
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.homePath()}">Главная</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
