plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.6.0"
}

repositories {
    mavenCentral()
    maven ("https://repo.aikar.co/content/groups/aikar/maven/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.5.3")
    // ponytail: repo.aikar.co no longer serves acf artifacts (404 on metadata paths);
    // the 0.5.1-SNAPSHOT acf-paper fat jar (BaseCommand + BukkitCommandManager + annotations,
    // no MinecraftReflection probe → 1.21-safe) is vendored under libs/ as a file dependency.
    implementation(files("libs/acf-paper-0.5.1-SNAPSHOT.jar"))

}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
    shadowJar {
        relocate("com.zaxxer.hikari", "dev.lorenzz.libs.hikari")
        relocate("org.mariadb", "dev.lorenzz.libs.mariadb")
        relocate("co.aikar.commands", "dev.lorenzz.libs.acf")
        relocate("co.aikar.locales", "dev.lorenzz.libs.acf.locales")
        mergeServiceFiles()
        exclude("org/slf4j/**")
    }
}