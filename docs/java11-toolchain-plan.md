# Plan: Java 11 Baseline + JDK 11 Toolchain

## Summary
Цель трека: убрать компиляторные предупреждения `source/target 8 obsolete` и зафиксировать воспроизводимую сборку, подняв baseline до Java 11 и закрепив toolchain JDK 11.

## Scope
- Обновить Java baseline в `:libs` до 11.
- Зафиксировать компиляцию и запуск unit tests на JDK 11 toolchain.
- Актуализировать документацию требований.
- Проверить отсутствие предупреждений `source/target 8 obsolete`.

## Implementation
1. `libs/build.gradle.kts`
- `sourceCompatibility`/`targetCompatibility` -> `JavaVersion.VERSION_11`.
- Для `JavaCompile`:
  - установить `javaCompiler` из toolchain JDK 11;
  - не использовать `--release` (ограничение AGP), опираться на `source/target=11`.
- Для `Test`:
  - установить `javaLauncher` из toolchain JDK 11.
2. `gradle.properties`
- удалить `android.javaCompile.suppressSourceTargetDeprecationWarning=true` как больше не нужный workaround.
3. Документация
- `README.md`: заменить требование Java 1.8 на Java 11+ и добавить примечание про JDK 11 для сборки.
- `CHANGES.txt`: добавить запись о переходе baseline/toolchain.

## Acceptance
- В `libs/build.gradle.kts` нет `VERSION_1_8`.
- Команды проходят успешно:
  - `./gradlew :libs:testDebugUnitTest --warning-mode all --console=plain`
  - `./gradlew :libs:compileDebugAndroidTestJavaWithJavac --warning-mode all --console=plain`
  - `./gradlew :libs:test --warning-mode all --console=plain`
- В логах нет предупреждений:
  - `source value 8 is obsolete`
  - `target value 8 is obsolete`
