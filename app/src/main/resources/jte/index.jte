@import hexlet.code.dto.MainPage
@import hexlet.code.util.NamedRoutes

@param MainPage page

@template.layout.page(
page = page,
content = @`
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
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)
