package yg0r2.examples

import io.spring.gradle.dependencymanagement.DependencyManagementPlugin
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.exclude
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import yg0r2.examples.generate.FileDataExtension
import yg0r2.examples.generate.GenerateTask
import yg0r2.examples.model.Dependency
import java.lang.IllegalArgumentException

class MyPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.group = "yg0r2.examples"

        // Depending on other plugin
        project.allprojects.forEach {
            if (!it.name.endsWith("ui")) {
                it.plugins.apply {
                    apply(DependencyManagementPlugin::class.java)
                    apply(JavaPlugin::class.java)
                    apply(SpringBootPlugin::class.java)
                }

                it.repositories.addAll(listOf(
                    project.repositories.mavenLocal(),
                    project.repositories.mavenCentral()
                ))
            }
        }

        // Execute in case of JavaPlugin present
        val javaPluginAction = Action<JavaPlugin> {
            println("Jee JavaPlugin found :)")
        }
        project.plugins.withType(JavaPlugin::class.java, javaPluginAction)

        // Add project dependencies
        val configuration = project.configurations.getByName("implementation")
        configuration.dependencies.addAll(listOf(
            project.dependencies.create("org.springframework.boot:spring-boot-starter-data-redis"),
            project.dependencies.create("org.springframework.boot:spring-boot-starter-security"),
            project.dependencies.create("org.springframework.boot:spring-boot-starter-thymeleaf"),
            project.dependencies.create("org.springframework.boot:spring-boot-starter-web"),
            project.dependencies.create("org.springframework.session:spring-session-data-redis")
        ))

        project.configurations.getByName("testImplementation").dependencies.addAll(listOf(
            project.dependencies.create("org.springframework.boot:spring-boot-starter-test")
        ))

        // Excluding dependency https://docs.gradle.org/current/userguide/resolution_rules.html
        val excludedDependencies = readJsonResource<List<Dependency>>("excludedDependencies.json")
        project.configurations.all {
            excludedDependencies.forEach {
                exclude(it.group, it.module)
            }
        }

        // Gradle automatically use `junit-jupiter` from any (transitive) dependencies instead of `junit`
//        project.dependencies.modules {
//            module("junit:junit") {
//                replacedBy("org.junit.jupiter:junit-jupiter", "junit is deprecated, have to use junit-jupiter")
//            }
//        }

        val fileDataExtension = project.extensions.create("fileData", FileDataExtension::class.java, project)
        project.tasks.register("generate", GenerateTask::class.java) {
            content.set(fileDataExtension.content)
            fileCount.set(fileDataExtension.fileCount)
        }

        project.repositories.addAll(listOf(
            project.repositories.mavenLocal(),
            project.repositories.mavenCentral()
        ))

        project.tasks.named("test", Test::class.java) {
            useJUnitPlatform()
        }

        project.configure<JavaPluginConvention> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    private inline fun <reified T> readJsonResource(resourcePath: String): T {
        val url = javaClass.classLoader.getResource(resourcePath) ?: throw IllegalArgumentException("Resource does not exist: $resourcePath")

        return Json.decodeFromString(url.readText())
    }

}
