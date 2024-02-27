@import hexlet.code.dto.MainPage
@import hexlet.code.util.NamedRoutes

@param MainPage page

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
    <h1>Анализатор страниц</h1>
    <h3>Бесплатно проверяйте сайты на SEO пригодность</h3>
    <form action="${NamedRoutes.homePath()}" method="POST">
        <label for="url">Введите адрес:</label>
        <input type="text" id="url" name="url" placeholder="ссылка" required>
        <button type="submit">Проверить</button>
    </form>
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.homePath()}">Главная</a></p>
    <p><a href="${NamedRoutes.urlsPath()}">Сайты</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
