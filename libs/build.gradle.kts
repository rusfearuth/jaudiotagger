import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

plugins {
    id("com.android.library")
    `maven-publish`
}

group = "io.github.rusfearuth"
version = "3.0.4"

android {
    namespace = "org.jaudiotagger"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        getByName("main") {
            java.setSrcDirs(listOf("src/main/java"))
            manifest.srcFile("src/main/AndroidManifest.xml")
        }
        getByName("test") {
            java.setSrcDirs(listOf("src/test/java"))
        }
        getByName("androidTest") {
            java.setSrcDirs(listOf("src/androidTest/java"))
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
}

val javaToolchains = extensions.getByType(JavaToolchainService::class.java)

tasks.withType<JavaCompile>().configureEach {
    javaCompiler.set(
        javaToolchains.compilerFor {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    )
    exclude("org/jaudiotagger/test/**")
    exclude("org/jaudiotagger/audio/flac/FlacHeaderTest.java")
    exclude("org/jaudiotagger/issues/Issue224Test.java")
    exclude("org/jaudiotagger/tag/id3/NewInterfaceTest.java")
    exclude("org/jaudiotagger/tag/id3/UnsynchronizationTest.java")
    exclude("org/jaudiotagger/tag/mp4/M4aReadTagTest.java")
    exclude("org/jaudiotagger/tag/vorbiscomment/VorbisImageTest.java")
    exclude("org/jaudiotagger/tag/vorbiscomment/VorbisWriteTagTest.java")
    exclude("org/jaudiotagger/tag/wma/WmaSimpleTest.java")
}

tasks.withType<Test>().configureEach {
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
    )
    useJUnit()
    // Legacy tests resolve fixtures via paths relative to the repository root.
    workingDir = rootProject.projectDir
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "io.github.rusfearuth"
                artifactId = "jaudiotagger"
                version = "3.0.4"

                pom {
                    name.set("jaudiotagger")
                    description.set("Java API for reading and writing audio metadata tags.")
                    url.set("https://github.com/rusfearuth/jaudiotagger")
                    licenses {
                        license {
                            name.set("LGPL-2.1")
                            url.set("https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html")
                        }
                    }
                    developers {
                        developer {
                            id.set("rusfearuth")
                            name.set("rusfearuth")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/rusfearuth/jaudiotagger.git")
                        developerConnection.set("scm:git:ssh://github.com/rusfearuth/jaudiotagger.git")
                        url.set("https://github.com/rusfearuth/jaudiotagger")
                    }
                }
            }
        }
    }
}
