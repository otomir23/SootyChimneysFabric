pluginManagement {
    val loom_version: String by settings
    val kotlin_version: String by settings
    val ksp_version: String by settings
    val minotaur_version: String by settings
    val curseforge_publish_version: String by settings

    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }

    plugins {
        id("fabric-loom") version loom_version
        id("org.jetbrains.kotlin.jvm") version kotlin_version
        id("com.google.devtools.ksp") version ksp_version
        id("com.modrinth.minotaur") version minotaur_version
        id("io.github.themrmilchmann.curseforge-publish") version curseforge_publish_version
    }
}