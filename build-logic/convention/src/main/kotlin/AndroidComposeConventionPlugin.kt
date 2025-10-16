import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import com.xbot.convetion.applyPlugin
import com.xbot.convetion.configureAndroidCompose
import com.xbot.convetion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin(libs.plugins.kotlin.compose)

            listOf(ApplicationExtension::class, LibraryExtension::class)
                .firstNotNullOf { type -> extensions.findByType(type) }
                .also { extension -> configureAndroidCompose(extension) }
        }
    }
}