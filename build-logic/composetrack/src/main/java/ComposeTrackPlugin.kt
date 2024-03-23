import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import com.hym.composetrack.ModifyAndroidComposeViewClassVisitorFactory
import com.hym.composetrack.ModifyClickableClassVisitorFactory
import com.hym.composetrack.ModifyTraceClassVisitorFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.register

/**
 * @author hehua2008
 * @date 2024/3/23
 */

/**
 * This custom plugin will register a callback that is applied to all variants.
 */
class ComposeTrackPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        // Registers a callback on the application of the Android Application plugin.
        // This allows the ComposeTrackPlugin to work whether it's applied before or after
        // the Android Application plugin.
        project.plugins.withType(AppPlugin::class.java) {

            // Queries for the extension set by the Android Application plugin.
            // This is the second of two entry points into the Android Gradle plugin
            val androidComponents =
                project.extensions.getByType(ApplicationAndroidComponentsExtension::class.java)
            // Registers a callback to be called, when a new variant is configured
            androidComponents.onVariants { variant ->
                // Call the transformClassesWith API: supply the class visitor factory, and specify the scope and
                // parameters
                /*
                variant.instrumentation.transformClassesWith(
                    ExampleClassVisitorFactory::class.java,
                    InstrumentationScope.PROJECT
                ) { params ->
                    params.newMethodName.set("transformedMethod")
                }
                */

                variant.instrumentation.transformClassesWith(
                    ModifyClickableClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) { }

                variant.instrumentation.transformClassesWith(
                    ModifyAndroidComposeViewClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) { }

                variant.instrumentation.transformClassesWith(
                    ModifyTraceClassVisitorFactory::class.java,
                    InstrumentationScope.ALL
                ) { }

                // -- Verification --
                // the following is just to validate the recipe and is not actually part of the recipe itself
                val taskName = "check${variant.name.capitalized()}AsmTransformation"
                val taskProvider = project.tasks.register<CheckAsmTransformationTask>(taskName) {
                    output.set(
                        project.layout.buildDirectory.dir("intermediates/$taskName")
                    )
                }

                // This creates a dependency on the classes in the project scope, which will run the
                // necessary tasks to build the classes artifact and trigger the transformation
                variant.artifacts
                    .forScope(ScopedArtifacts.Scope.ALL)
                    .use(taskProvider)
                    .toGet(
                        ScopedArtifact.CLASSES,
                        CheckAsmTransformationTask::inputJars,
                        CheckAsmTransformationTask::inputDirectories,
                    )
            }
        }
    }
}
