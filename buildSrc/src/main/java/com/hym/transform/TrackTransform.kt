package com.hym.transform

import com.android.build.api.transform.*
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * @author hehua2008
 * @date 2022/1/5
 */
class TrackTransform(project: Project) : BaseTransform(project) {
    companion object {
        private const val TRACE = "androidx/compose/runtime/Trace.class"
        private const val UI_APPLIER = "androidx/compose/ui/node/UiApplier.class"
        private const val ANDROID_COMPOSE_VIEW =
            "androidx/compose/ui/platform/AndroidComposeView.class"
        private const val CLICKABLE = "androidx/compose/foundation/gestures/TapGestureDetectorKt.class"
        private val TARGETS = arrayOf(TRACE, UI_APPLIER, ANDROID_COMPOSE_VIEW, CLICKABLE)
    }

    override fun getName(): String = "compose.track"

    override fun doTransformJar(jarInput: JarInput, destOutput: File): Boolean {
        JarFile(jarInput.file).use { jarFile ->
            val entries = jarFile.entries().toList()
            val filter = entries.filter {
                TARGETS.contains(it.name)
            }
            if (filter.isEmpty()) return false
            // Write the modified bytecode directly to destOutput.
            JarOutputStream(FileOutputStream(destOutput)).use { jos ->
                entries.forEach { other ->
                    if (filter.contains(other)) return@forEach
                    jos.putNextEntry(JarEntry(other.name))
                    jarFile.getInputStream(other).use {
                        jos.write(it.readAllBytes())
                    }
                    jos.closeEntry()
                }

                filter.forEach { targetEntry ->
                    log("found ${targetEntry.name} in ${jarInput.file}")
                    jos.putNextEntry(JarEntry(targetEntry.name))
                    jarFile.getInputStream(targetEntry).use {
                        when (targetEntry.name) {
                            TRACE -> ModifyTrace.modifyByteCode(it)
                            //UI_APPLIER -> ModifyUiApplier.modifyByteCode(it)
                            ANDROID_COMPOSE_VIEW -> ModifyAndroidComposeView.modifyByteCode(it)
                            CLICKABLE -> ModifyClickable.modifyByteCode(it)
                            else -> it.readAllBytes()
                        }
                    }.let {
                        jos.write(it)
                    }
                    jos.closeEntry()
                }

                log("write to $destOutput")
            }
            return true
        }
    }

    override fun doTransformFile(fileInput: File, destOutput: File): Boolean = false
}