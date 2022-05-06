package com.hym.transform

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author hehua2008
 * @date 2022/1/5
 */
class TracePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Welcome to compose track plugin...")
        val ext = project.extensions.getByType(BaseExtension::class.java)
        ext.registerTransform(TrackTransform(project))
    }
}