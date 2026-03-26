# Lamoda UI Tests

Проект содержит use-case модель и набор автоматизированных UI-тестов для `https://www.lamoda.ru` на Java + Selenium.

## Что внутри

- `docs/use-case-diagram.puml` - use-case диаграмма для Lamoda.
- `docs/use-case-diagram.drawio` - use-case диаграмма для draw.io.
- `docs/use-cases.md` - перечень прецедентов и трассировка к тестам.
- `docs/lamoda.side` - шаблоны Selenium IDE с XPath-локаторами.
- `src/test/java/...` - Java/Selenium smoke и e2e тесты для `Chrome` и `Firefox`.

## Покрываемые пользовательские сценарии

- просмотр главной страницы;
- поиск товара;
- просмотр каталога и элементов фильтрации;
- просмотр карточки товара;
- просмотр корзины.

## Запуск

Требования:

- Java 17 или 21
- Gradle 8+
- установленный Chrome или Firefox

Примеры запуска:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test -Dbrowser=chrome -Dheadless=false
JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew test -Dbrowser=firefox -Dheadless=false
```

Дополнительные параметры:

- `-DbaseUrl=https://www.lamoda.ru`
- `-DtimeoutSeconds=20`
- `-Dheadless=false`
- `-Dbrowser=firefox`

## Замечание по Selenium RC

В исходном задании упомянут Selenium RC, но этот стек давно снят с поддержки. Поэтому исполняемая часть реализована на актуальном Selenium WebDriver для Java + Gradle, а IDE-шаблоны вынесены в `docs/lamoda.side`. Локаторы в тестах заданы через `XPath`, как требует задание.

## Ограничение production-сайта

На момент подготовки проекта `lamoda.ru` защищен anti-bot страницей `Qrator`, поэтому в автоматизированной среде сайт может отдавать не каталог, а challenge-страницу. Теперь тесты не пропускаются молча: при таком ответе они падают с явным сообщением о том, что сайт вернул anti-bot page.

Отдельно: запуск через `./gradlew` был проверен на `JDK 21`. На более новых runtime, например `Java 24`, возможны проблемы уже на стороне Gradle, а не самих тестов.

Практически для `lamoda.ru` лучше запускать тесты в обычном видимом браузере. `Headless` и `Chrome` чаще попадают под anti-bot, чем `Firefox` в headful-режиме.

## Почему раньше тесты игнорировались

В предыдущей версии проекта в [BaseTest.java](/Users/montana/MyLabs/tpo/tpo_lab_3/src/test/java/ru/itmo/tpo/lamoda/core/BaseTest.java) стоял `Assumptions.assumeTrue(...)`, который отключал live-тесты по умолчанию. Это поведение убрано.
