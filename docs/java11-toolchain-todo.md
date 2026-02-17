# TODO: Java 11 Baseline + JDK 11 Toolchain

## Build config
- [x] Обновить `sourceCompatibility`/`targetCompatibility` до `JavaVersion.VERSION_11`.
- [x] Закрепить `JavaCompile` на JDK 11 toolchain.
- [x] Закрепить `Test` на JDK 11 toolchain.
- [x] Не использовать `options.release = 11` для Android `JavaCompile` (AGP запрещает `--release`; эквивалент достигнут через `source/target=11` + toolchain 11).
- [x] Удалить `android.javaCompile.suppressSourceTargetDeprecationWarning` из `gradle.properties`.

## Documentation
- [x] Обновить `README.md` (Java baseline и build JDK).
- [x] Обновить `CHANGES.txt` записью про migration to Java 11 baseline.

## Validation
- [x] `./gradlew :libs:testDebugUnitTest --warning-mode all --console=plain`
- [x] `./gradlew :libs:compileDebugAndroidTestJavaWithJavac --warning-mode all --console=plain`
- [x] `./gradlew :libs:test --warning-mode all --console=plain`

## Result criteria
- [x] В логах нет `source value 8 is obsolete`.
- [x] В логах нет `target value 8 is obsolete`.
