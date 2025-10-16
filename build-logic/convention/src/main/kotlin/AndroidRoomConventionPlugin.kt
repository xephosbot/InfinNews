import com.google.devtools.ksp.gradle.KspExtension
import com.xbot.convetion.applyPlugin
import com.xbot.convetion.implementation
import com.xbot.convetion.ksp
import com.xbot.convetion.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidRoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            applyPlugin(libs.plugins.kotlin.ksp)

            extensions.configure<KspExtension> {
                arg("room.generateKotlin", "true")
            }

            dependencies {
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.ktx)
                ksp(libs.androidx.room.compiler)
            }
        }
    }
}