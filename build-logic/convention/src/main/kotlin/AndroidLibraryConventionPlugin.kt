import com.android.build.api.dsl.LibraryExtension
import com.xbot.convetion.applyPlugin
import com.xbot.convetion.configureKotlinAndroid
import com.xbot.convetion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin(libs.plugins.android.library)
            applyPlugin(libs.plugins.kotlin.android)
            applyPlugin(libs.plugins.infinnews.detekt)

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
            }
        }
    }
}