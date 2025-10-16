import com.xbot.convetion.applyPlugin
import com.xbot.convetion.detektPlugins
import com.xbot.convetion.libs
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin(libs.plugins.detekt)

            extensions.configure<DetektExtension> {
                parallel = true
                config.setFrom("$rootDir/config/detekt/detekt.yml")
            }

            dependencies {
                detektPlugins(libs.detekt.formatting)
                detektPlugins(libs.detekt.rules.compose)
            }
        }
    }
}