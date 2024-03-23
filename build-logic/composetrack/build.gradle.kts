plugins {
    java
    //groovy
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    //implementation(localGroovy())
    //implementation(gradleApi())
    compileOnly(libs.android.gradle.api)
    //compileOnly(libs.android.gradle)
    implementation(gradleKotlinDsl())
    //implementation(libs.kotlin.gradle.plugin)
    //implementation(libs.javapoet)
    //implementation(libs.commons.io)
    //implementation(libs.commons.codec)
    implementation(libs.asm)
    implementation(libs.asm.tree)
    //implementation(libs.asm.util)
}

gradlePlugin {
    plugins {
        create("composeTrackPlugin") {
            id = "compose.track"
            implementationClass = "ComposeTrackPlugin"
        }
    }
}
