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
    <h1>Добавить адрес</h1>
    <form action="${NamedRoutes.homePath()}" method="POST">
        <label for="url">Введите адрес:</label>
        <input type="text" id="url" name="url" required>
        <button type="submit">Добавить</button>
    </form>
    <p><a href="${NamedRoutes.urlsPath()}">Список добавленных URL</a></p>
    </body>
`,
footer = @`
    <p><a href="${NamedRoutes.homePath()}">Главная</a></p>
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
