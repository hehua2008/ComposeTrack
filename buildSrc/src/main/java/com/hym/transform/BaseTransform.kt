package com.hym.transform

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import java.io.File

/**
 * @author hehua2008
 * @date 2022/1/5
 */
abstract class BaseTransform(protected val project: Project) : Transform() {
    protected val tag = javaClass.simpleName

    protected fun log(msg: String) {
        project.logger.lifecycle("$tag: $msg")
    }

    abstract override fun getName(): String

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    final override fun isIncremental(): Boolean = true

    /**
     * @return true if transformed jar has been written to destOutput, else false.
     */
    abstract fun doTransformJar(jarInput: JarInput, destOutput: File): Boolean

    /**
     * @return true if transformed file has been written to destOutput, else false.
     */
    abstract fun doTransformFile(fileInput: File, destOutput: File): Boolean

    final override fun transform(transformInvocation: TransformInvocation) {
        log("start transform")

        val isIncremental = transformInvocation.isIncremental

        // Consumer input, the jar and class directory path can be obtained from it,
        // and it needs to be output to the next transformer.
        val transformInputs = transformInvocation.inputs

        // Reference input, no output required.
        val referenceInputs = transformInvocation.referencedInputs

        // outputProvider manage output paths.
        // If transformInputs is empty, outputProvider will be null.
        val outputProvider = transformInvocation.outputProvider

        // The source file of the sub-module will also generate a jar during the compilation process
        // and then be compiled into the main project.
        transformInputs.forEach { input ->
            input.jarInputs.forEach jarInputs@{ jarInput ->
                //log("jarInput $jarInput")

                val destJar = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )

                if (isIncremental) {
                    when (jarInput.status) {
                        Status.REMOVED -> {
                            log("delete $destJar")
                            FileUtils.deleteQuietly(destJar)
                            return@jarInputs
                        }
                        Status.NOTCHANGED -> return@jarInputs
                        Status.ADDED, Status.CHANGED -> {}
                    }
                }

                if (!doTransformJar(jarInput, destJar)) {
                    // Copy the unmodified bytecode to dest.
                    FileUtils.copyFile(jarInput.file, destJar)
                }
            }

            input.directoryInputs.forEach { directoryInput ->
                //log("directoryInput ${directoryInput.file}")

                val destDir = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )

                if (isIncremental) {
                    directoryInput.changedFiles.forEach changedFiles@{ (file, status) ->
                        val destFile = File(destDir, file.relativeTo(directoryInput.file).path)
                        when (status) {
                            Status.REMOVED -> {
                                log("delete $destFile")
                                FileUtils.deleteQuietly(destFile)
                                return@changedFiles
                            }
                            Status.NOTCHANGED -> return@changedFiles
                            Status.ADDED, Status.CHANGED -> {
                                if (!doTransformFile(file, destFile)) {
                                    // Copy the unmodified bytecode to dest.
                                    log("copy to $destFile")
                                    FileUtils.copyFile(file, destFile)
                                }
                            }
                        }
                    }
                } else {
                    FileUtils.deleteQuietly(destDir)
                    FileUtils.iterateFiles(directoryInput.file, null, true).forEach { file ->
                        val destFile = File(destDir, file.relativeTo(directoryInput.file).path)
                        if (!doTransformFile(file, destFile)) {
                            // Copy the unmodified bytecode to dest.
                            //log("copy to $destFile")
                            FileUtils.copyFile(file, destFile)
                        }
                    }
                }
            }
        }

        log("end transform")
    }
}