# Publishing to GitHub Packages

## Target coordinates

- `groupId`: `io.github.rusfearuth`
- `artifactId`: `jaudiotagger`
- `version`: `3.0.2`
- `repository`: `https://maven.pkg.github.com/rusfearuth/jaudiotagger`

## Prerequisites

1. GitHub token with `write:packages` and repository access.
2. Credentials in environment variables or Gradle properties.

Example (`~/.gradle/gradle.properties`):

```properties
gpr.user=<github-username>
gpr.key=<github-token>
```

## Publish command

```bash
./gradlew :libs:publish
```

## Verify publication tasks

```bash
./gradlew :libs:tasks --all | rg publish
```

## Common failures

- `401 Unauthorized`
  - Invalid token or missing package scope permissions.
- `403 Forbidden`
  - Token does not have access to `rusfearuth/jaudiotagger` repository.
- `Could not find property gpr.user/gpr.key`
  - Provide `GITHUB_ACTOR`/`GITHUB_TOKEN` or Gradle properties.
