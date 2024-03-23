import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

/**
 * This task does a trivial verification of the ASM transformation on the classes.
 */
abstract class CheckAsmTransformationTask : DefaultTask() {

    // In order for the task to be up-to-date when the inputs have not changed,
    // the task must declare an output, even if it's not used. Tasks with no
    // output are always run regardless of whether the inputs changed
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    /**
     * Project scope, not including dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val projectDirectories: ListProperty<Directory>

    /**
     * Project scope, not including dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val projectJars: ListProperty<RegularFile>

    @TaskAction
    fun taskAction() {
        val someSourceClassFile =
            projectDirectories.get().first()
                .file("com/composetrack/demo/SomeSource.class")
        val classReader = ClassReader(someSourceClassFile.asFile.readBytes())
        val classNode = ClassNode(Opcodes.ASM9)
        classReader.accept(classNode, 0)
        if (classNode.methods.find { it.name == "transformedMethod" } == null) {
            throw RuntimeException("Transformed method not found.")
        }
    }
}
