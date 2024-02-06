@import hexlet.code.dto.MainPage
@import hexlet.code.util.NamedRoutes

@param MainPage page

@template.layout.page(
content = @`
    <!DOCTYPE html>
    <html lang="ru">
    <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>Привет Hexlet!</title>
    </head>
    <body>
    <main>
        <div>
            <p><a href="${NamedRoutes.buildSessionPath()}">Войти</a></p>
        </div>
        <div>
            <p><a href="${NamedRoutes.carsPath()}">Страница Автомобилей</a></p>
        </div>
        <div>
            <p><a href="${NamedRoutes.coursesPath()}">Страница Курсов</a></p>
        </div>
        <div>
            <p><a href="${NamedRoutes.usersPath()}">Страница Пользователей</a></p>
        </div>
        <h1>Привет, Хекслет!</h1>
        @if(page.getCurrentUser() != null)
            Добро пожаловать, ${page.getCurrentUser()}.
            Чтобы разлогиниться, удалите куку JSESSIONID из браузера
            <div>
                <p><a href="${NamedRoutes.sessionsPath()}">Выйти</a></p>
            </div>
        @endif
    </main>
    </body>
    </html>
`,
footer = @`
    <p><a href="https://github.com/ArsenHandzhyan">github аккаунт</a></p>
`
)