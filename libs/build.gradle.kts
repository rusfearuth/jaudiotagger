import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

plugins {
    id("com.android.library")
}

android {
    namespace = "org.jaudiotagger"
    compileSdk = 35

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
}

dependencies {
    testImplementation("junit:junit:3.8.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
}

tasks.withType<JavaCompile>().configureEach {
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
    // Legacy tests resolve fixtures via paths relative to the repository root.
    workingDir = rootProject.projectDir
}
