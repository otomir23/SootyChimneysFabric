plugins {
    id("fabric-loom")
    `maven-publish`
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
}

fun prop(name: String): String = project.properties[name] as String

version = prop("mod_version")
group = prop("maven_group")

repositories {
    maven("https://mvn.devos.one/snapshots/")
    maven("https://maven.tterrag.com/")
    maven("https://jitpack.io/")
    maven("https://api.modrinth.com/maven")
    maven("https://maven.jamieswhiteshirt.com/libs-release")
    maven("https://cursemaven.com")
}

dependencies {
    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${prop("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${prop("loader_version")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${prop("fabric_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${prop("fabric_kotlin_version")}")

    modApi("com.simibubi.create:create-fabric-${prop("minecraft_version")}:${prop("create_version")}")
    include(modImplementation("maven.modrinth:midnightlib:${prop("midnightlib_version")}")!!)!!
}

base {
    archivesBaseName = prop("archives_base_name")
}

tasks.processResources {
    val properties = mapOf(
        "version" to prop("version"),
        "minecraft_version" to prop("minecraft_version"),
        "loader_version" to prop("loader_version"),
        "fabric_version" to prop("fabric_version"),
        "fabric_kotlin_version" to prop("fabric_kotlin_version"),
        "create_version" to prop("create_version")
    )
    properties.forEach { (k, v) -> inputs.property(k, v) }

    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(properties)
    }
}

// Minecraft 1.18 (1.18-pre2) upwards uses Java 17.
val targetJavaVersion = JavaVersion.VERSION_17
tasks.withType<JavaCompile>().configureEach {
    // ensure that the encoding is set to UTF-8, no matter what the system default is
    // this fixes some edge cases with special characters not displaying correctly
    // see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
    // If Javadoc is generated, this must be specified in that task too.
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion.ordinal + 1)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = targetJavaVersion
    targetCompatibility = targetJavaVersion
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${prop("archivesBaseName")}"}
    }
}

// configure the maven publication
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
    repositories {
        // Add repositories to publish to here.
        // Notice: This block does NOT have the same function as the block in the top level.
        // The repositories here will be used for publishing your artifact, not for
        // retrieving dependencies.
    }
}

tasks.compileKotlin { kotlinOptions.jvmTarget = targetJavaVersion.majorVersion }