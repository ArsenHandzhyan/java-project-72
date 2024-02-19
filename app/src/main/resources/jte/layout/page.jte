@import gg.jte.Content
@import hexlet.code.dto.BasePage
@param BasePage page = null
@param Content content
@param Content footer

<!doctype html>
<html lang="ru">
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
@if(page != null && page.getFlash() != null)
    <div class="alert ${page.getFlashType()}" role="alert">
        <p>${page.getFlash()}</p>
    </div>
@endif
@if (footer != null)
    <div class="footer">
        ${footer}
    </div>
@endif
<div class="content">
    ${content}
</div>
</body>
</html>