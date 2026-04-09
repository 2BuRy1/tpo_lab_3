# Advego Use-Case Coverage

Тестовое покрытие построено по use-case диаграмме из `docs/use-case-diagram.drawio` и дополнено авторизованными сценариями для аккаунта заказчика и исполнителя. Во всех сценариях используются XPath-локаторы и проверяются реальные данные интерфейса, а не только факт открытия страницы.

## Покрытые ветки

| Ветка use-case | Реальный сценарий | Тест |
| --- | --- | --- |
| Логин/активация аккаунта | Вход по валидным учетным данным и проверка авторизованной шапки | `LoginTest.shouldLoginWithValidCredentials` |
| Просмотр инструментов | Открытие каталога сервисов и проверка доступности ключевых инструментов | `ServicesCatalogTest.shouldExposeGuestToolsFromUseCaseDiagram` |
| Проверка орфографии | Негативная валидация пустого текста перед отправкой | `SpellCheckPageTest.shouldShowValidationErrorWhenTextIsEmpty` |
| SEO анализ текста | Проверка детерминированных метрик для текста `мама мыла раму.` | `SeoAnalysisPageTest.shouldCalculateDeterministicMetricsForSimpleRussianText` |
| Конвертер регистров | Нижний, верхний, первая заглавная, все слова с заглавной | `TextProcessingToolsTest.shouldConvertTextRegisterAcrossSupportedModes` |
| Исправление текста в неправильной раскладке | Конвертация Latin -> Russian и Russian -> Latin | `TextProcessingToolsTest.shouldFixKeyboardLayoutInBothDirections` |
| Поиск слов в тексте | Поиск слова, счетчик совпадений и подсветка найденных вхождений | `TextProcessingToolsTest.shouldFindExactWordMatchesInText` |
| Генератор паролей | Генерация пароля по выбранной политике состава и длины | `MarketingToolsTest.shouldGeneratePasswordThatMatchesSelectedPolicy` |
| Калькулятор CTR | Расчет CTR по числу показов и кликов | `MarketingToolsTest.shouldCalculateCtrAsPercentage` |
| Просмотр извещений | Проверка, что список извещений содержит реальные события входа и IP-адреса | `AccountAreaTest.shouldShowSecurityRelevantLoginNotices` |
| Редактирование профиля | Проверка ограничений редактирования, доступности кнопки сохранения и дефолтных настроек профиля | `AccountAreaTest.shouldExposeProfileEditingConstraintsForNewExecutor` |
| Поиск доступных заказов исполнителем | Проверка счетчика заказов, первого заказа, цены и возможности взять работу | `ExecutorWorkflowTest.shouldShowRealAvailableOrdersForExecutor` |
| Просмотр раздела "Мои работы" | Проверка статистики исполнителя и доступности переходов к поиску работы и продаже статьи | `ExecutorWorkflowTest.shouldKeepExecutorNavigationAvailableWithoutActiveJobs` |
| Продажа статьи | Проверка формы продажи статьи, обязательных полей, защитных флагов и условий публикации без отправки формы | `ExecutorWorkflowTest.shouldPrepareArticleSellingFormWithRequiredControls` |
| Просмотр списка заказов заказчика | Проверка вкладок раздела заказов, ссылки на создание заказа и пустого состояния | `CustomerWorkflowTest.shouldShowCustomerOrdersSectionWithExpectedNavigation` |
| Создание заказа | Негативная валидация пустой формы создания заказа с проверкой конкретных ошибок обязательных полей | `CustomerWorkflowTest.shouldValidateRequiredFieldsBeforeSavingNewOrder` |

## Частично покрытые ветки

- Создание статьи и публикация в магазине: форма продажи статьи покрыта до уровня подготовки и обязательных условий, но реальная отправка пока не включена, чтобы не создавать реальные сущности в рабочем аккаунте.
- Выполнение работы, подача заявки и завершение заказа: для безопасной автоматизации нужны отдельные тестовые заказы или специально подготовленный контур, чтобы тесты не брали в работу реальные задания и не меняли боевые данные аккаунта.

## Ограничения

- Сценарии не используют `id`-привязку в качестве основного способа поиска элементов; выбор элементов выполняется через XPath.
- Авторизованные сценарии требуют реальной учетной записи и запускаются только при наличии переменных окружения.
- Деструктивные действия не выполняются без гарантии безопасного отката данных.

## Запуск

```bash
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew test --no-daemon -Dbrowser=chrome -Dheadless=true
GRADLE_USER_HOME="$PWD/.gradle-home" ./gradlew test --no-daemon -Dbrowser=firefox -Dheadless=true
```

Для авторизованных сценариев необходимо заранее передать переменные окружения:

```bash
export ADVEGO_LOGIN="your-email"
export ADVEGO_PASSWORD="your-password"
```
