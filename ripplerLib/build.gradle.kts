import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
    id("signing")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm("desktop")

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.collection)
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}

android {
    namespace = "org.skobinsky.rippler"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

fun loadProperties(): Properties? {
    val keystorePropertiesFile = rootProject.file("local.properties")
    if (!keystorePropertiesFile.exists()) {
        return null
    }
    val keystoreProperties = Properties()
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    return keystoreProperties
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("io.github.gleb-skobinsky", "rippler", "1.0.2")

    pom {
        name = "Rippler"
        description = "Compose multiplarform ripple indications library"
        inceptionYear = "2025"
        url = "https://github.com/gleb-skobinsky/Rippler"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "skobinsky"
                name = "Gleb Gutnik"
                url = "https://github.com/gleb-skobinsky"
            }
        }
        scm {
            url = "https://github.com/gleb-skobinsky/Rippler"
            connection = "scm:git:git://github.com/gleb-skobinsky/Rippler.git"
            developerConnection =
                "scm:git:ssh://git@github.com/gleb-skobinsky/Rippler.git"
        }
    }
}

signing {
    val props = loadProperties()
    // null for CI/CD builds
    props?.let {
        useInMemoryPgpKeys(
            props["signing.keyId"].toString(),
            File(props["signing.secretKeyFile"].toString()).readText(),
            props["signing.password"].toString()
        )
    }
}