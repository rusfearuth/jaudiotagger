# Jaudiotagger

*Jaudiotagger* — это Java API для работы с аудио-метаданными. Доступны как общий API, так и API для конкретных форматов. В настоящее время поддерживаются чтение и запись метаданных для:

- Mp3
- Flac
- OggVorbis
- Mp4
- Aiff
- Wav
- Wma
- Dsf

Основная страница проекта: http://www.jthink.net/jaudiotagger/. С основным разработчиком можно связаться по email: paultaylor@jthink.net.

## Требования

Для *Jaudiotagger* требуется Java 1.8.

## Участие в разработке

*Jaudiotagger* приветствует контрибьюторов: если вы внесете улучшение или исправление ошибки, мы с высокой вероятностью быстро примем изменения в основную ветку.

Если вы не можете вносить код, но хотите поддержать проект, рассмотрите возможность пожертвования:
[here](http://www.jthink.net/jaudiotagger/donate.jsp).

## Подключение в ваш проект

Последний релиз — 3.0.1, доступен в Maven Central. Чтобы использовать библиотеку, добавьте в `pom.xml` вашего приложения:

    `<dependency>
        <groupId>net.jthink</groupId>
        <artifactId>jaudiotagger</artifactId>
        <version>3.0.1</version>
    </dependency>
    `

## Сборка

Структура каталогов:

### Под системой контроля версий

- `src`                  : исходный код
- `srctest`              : тестовый исходный код
- `www`                  : JavaDoc
- `testdata`             : тестовые файлы для JUnit-тестов (не все тесты включены в дистрибутив из-за авторских прав)
- `target`               : содержит `jaudiotagger***.jar`, собранный Maven

### Файлы IDE

- `jaudiotagger.iml`     : модуль JetBrains IntelliJ
- `jaudiotagger.ipr`     : проект JetBrains IntelliJ

### Лицензия

- `license.txt` : файл лицензии

### Локальная сборка

Сборка выполняется с помощью [Maven](http://maven.apache.org).

   `pom.xml` : файл сборки Maven

Чтобы скомпилировать, запустить тесты, сгенерировать JavaDoc и установить основной JVM-артефакт в локальный репозиторий, выполните:

    mvn install

### Сборка всех вариантов библиотеки

Используйте команды ниже в зависимости от нужного варианта артефакта:

- Вариант по умолчанию для JVM (только компиляция):
  `mvn -DskipTests clean compile`
- Вариант по умолчанию для JVM (сборка JAR):
  `mvn -DskipTests clean package`
- Вариант, совместимый с Android (только компиляция):
  `mvn -Pandroid -DskipTests clean compile`
- Вариант, совместимый с Android (сборка JAR):
  `mvn -Pandroid -DskipTests clean package`
- Сборка обоих вариантов последовательно:
  `mvn -DskipTests clean package && mvn -Pandroid -DskipTests clean package`

### Android-сборка (API 28+)

Чтобы собрать Android-совместимый вариант без зависимостей от `java.desktop`, выполните:

    mvn -Pandroid -DskipTests clean compile

Этот профиль компилирует Android-safe переопределения из `src-android` и исключает desktop-only классы.

Для интеграции с SAF/`Uri` используйте `org.jaudiotagger.android.AndroidAudioFileIO`:

- `read(context, uri)`
- `readAs(context, uri, ext)`
- `write(context, audioFile, uri)`
- `delete(context, uri)`

`AndroidAudioFileIO` намеренно использует reflection, чтобы основной артефакт не требовал compile-time зависимостей от `android.*`.

### Релизная сборка

Чтобы собрать релизные артефакты (sources/javadocs/signing profile из `pom.xml`), выполните:

    mvn clean deploy -Prelease

Чтобы сгенерировать сайт *Jaudiotagger* вместе с отчетами по покрытию кода, выполните:

    mvn site

Он будет доступен по пути `target/site/index.html`.

Отчет по покрытию тестами доступен в `target/site/cobertura/index.html`.

### Примечания по релизу в Maven Central
- убрать `SNAPSHOT` из версии в `pom.xml`
- закоммитить `pom.xml`
- создать тег версии в формате `vx.x.x`
- выполнить `git push origin vx.x.x`
- выполнить `mvn clean deploy -Prelease`
- войти в https://s01.oss.sonatype.org/
- выпустить staging-релиз
- дождаться появления в Maven Central
- обновить `pom.xml`: увеличить версию и вернуть суффикс `SNAPSHOT`
