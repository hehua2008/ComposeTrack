import com.hym.composetrack.ANDROID_COMPOSE_VIEW_CLASS
import com.hym.composetrack.CLICKABLE_CLASS
import com.hym.composetrack.ClickableMethodList
import com.hym.composetrack.LAYOUT_NODE_CLASS
import com.hym.composetrack.LayoutNodeMethodList
import com.hym.composetrack.TRACE_CLASS
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import java.util.jar.JarEntry
import java.util.jar.JarFile

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
     * All scope, including dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val inputDirectories: ListProperty<Directory>

    /**
     * All scope, including dependencies.
     */
    @get:InputFiles
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val inputJars: ListProperty<RegularFile>

    @TaskAction
    fun taskAction() {
        /*
        val someSourceClassFile =
            projectDirectories.get().first()
                .file("com/composetrack/demo/SomeSource.class")
        val classReader = ClassReader(someSourceClassFile.asFile.readBytes())
        val classNode = ClassNode(Opcodes.ASM9)
        classReader.accept(classNode, 0)
        if (classNode.methods.find { it.name == "transformedMethod" } == null) {
            throw RuntimeException("Transformed method not found!")
        }
        */

        val classMap = mutableMapOf<String, Pair<JarFile, JarEntry>?>(
            CLICKABLE_CLASS to null,
            ANDROID_COMPOSE_VIEW_CLASS to null,
            TRACE_CLASS to null,
            LAYOUT_NODE_CLASS to null
        )
        var mapCount = 0
        outer@ for (jarFile in inputJars.get().map { JarFile(it.asFile) }) {
            for (jarEntry in jarFile.entries()) {
                val clazz = jarEntry.name
                if (classMap.containsKey(clazz)) {
                    val oldValue = classMap[clazz]
                    if (oldValue != null) {
                        throw RuntimeException("Duplicate class: $clazz in $jarFile and ${oldValue.first}")
                    }
                    classMap[clazz] = jarFile to jarEntry
                    if (++mapCount == classMap.size) break@outer
                }
            }
        }
        if (mapCount != classMap.size) {
            val notFoundList = classMap.filter { (clazz, jarFile) ->
                jarFile == null
            }.keys
            throw RuntimeException("Transformation not found in $notFoundList")
        }
        classMap.forEach { (clazz, jarFileEntry) ->
            val (jarFile, jarEntry) = jarFileEntry!!
            val bytes = jarFile.getInputStream(jarEntry).readBytes()
            val classReader = ClassReader(bytes)
            val classNode = ClassNode(Opcodes.ASM9)
            classReader.accept(classNode, 0)
            when (clazz) {
                CLICKABLE_CLASS -> {
                    val methodNames = mutableSetOf<String>()
                    classNode.methods.mapTo(methodNames) { it.name }
                    val untransformed = ClickableMethodList.subtract(methodNames)
                    if (untransformed.isNotEmpty()) {
                        throw RuntimeException("Not transformed methods in $clazz: $untransformed")
                    }
                    logger.log(LogLevel.WARN, "ComposeTrack transformed success: $clazz")
                }

                ANDROID_COMPOSE_VIEW_CLASS -> {
                    // TODO
                    logger.log(LogLevel.WARN, "ComposeTrack transformed success: $clazz")
                }

                TRACE_CLASS -> {
                    // TODO
                    logger.log(LogLevel.WARN, "ComposeTrack transformed success: $clazz")
                }

                LAYOUT_NODE_CLASS -> {
                    val allMethodsSignature = mutableSetOf<String>()
                    classNode.methods.mapTo(allMethodsSignature) { it.name + it.desc }
                    val different = LayoutNodeMethodList.subtract(allMethodsSignature)
                    if (different.isNotEmpty()) {
                        throw RuntimeException("$clazz has changed, please check these methods: $different")
                    }
                }
            }
        }
    }
}
