import com.android.build.api.dsl.ApplicationExtension
import com.xbot.convetion.applyPlugin
import com.xbot.convetion.configureKotlinAndroid
import com.xbot.convetion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin(libs.plugins.android.application)
            applyPlugin(libs.plugins.kotlin.android)
            applyPlugin(libs.plugins.infinnews.detekt)

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 36
            }
        }
    }
}