/*
 * Copyright (c) 2015 the authors of j2objc-gradle (see AUTHORS file)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.j2objccontrib.j2objcgradle.tasks

import com.github.j2objccontrib.j2objcgradle.J2objcConfig
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Assemble task copies generated source to assembly directories for
 * use by an iOS application.
 */
@CompileStatic
class AssembleSourceTask extends DefaultTask {

    // Generated ObjC source files
    @InputDirectory
    File srcGenDir

    @OutputDirectory
    File getDestSrcDir() {
        return project.file(destSrcDirPath)
    }

    @OutputDirectory
    File getDestSrcDirTest() {
        return project.file(destSrcDirTestPath)
    }

    // j2objcConfig dependencies for UP-TO-DATE checks

    // We keep these strings as @Input properties in addition to the @OutputDirectory methods above because,
    // for example, whether or not the main source and test source are identical affects execution of this task.
    @Input
    String getDestSrcDirPath() { return J2objcConfig.from(project).destSrcDir }

    @Input
    String getDestSrcDirTestPath() { return J2objcConfig.from(project).destSrcDirTest }

    @TaskAction
    void destCopy() {
        clearDestSrcDirWithChecks(destSrcDir, 'destSrcDir')
        copyMainSource()

        if (destSrcDirTest.absolutePath != destSrcDir.absolutePath) {
            // If we want main source and test source in one directory, then don't
            // re-delete the main directory where we just put files!
            clearDestSrcDirWithChecks(destSrcDirTest, 'destSrcDirTest')
        }
        copyTestSource()
    }


    private void clearDestSrcDirWithChecks(File destDir, String name) {
        ConfigurableFileCollection nonObjcDestFiles = project.files(project.fileTree(
                dir: destDir, excludes: ["**/*.h", "**/*.m"]))
        // Warn if deleting non-generated objc files from destDir
        nonObjcDestFiles.each { File file ->
            String message =
                    "Unexpected files in $name - this folder should contain ONLY j2objc\n" +
                    "generated files Objective-C. The folder contents are deleted to remove\n" +
                    "files that are nolonger generated. Please check the directory and remove\n" +
                    "any files that don't end with Objective-C extensions '.h' and '.m'.\n" +
                    "$name: ${destDir.path}\n" +
                    "Unexpected file for deletion: ${file.path}"
            throw new InvalidUserDataException(message)
        }
        // TODO: better if this was a sync operation as it does deletes automatically
        logger.debug("Deleting $name to fill with generated objc files... " + destDir.path)
        project.delete destDir
    }

    void copyMainSource() {
        Utils.projectCopy(project, {
            includeEmptyDirs = false
            from srcGenDir
            into destSrcDir
            // TODO: this isn't precise, main source can be suffixed with Test as well.
            // Would be best to somehow keep the metadata about whether a file was from the
            // main sourceset or the test sourceset.
            // Don't copy the test code
            exclude "**/*Test.h"
            exclude "**/*Test.m"
        })
    }

    void copyTestSource() {
        Utils.projectCopy(project, {
            includeEmptyDirs = false
            from srcGenDir
            into destSrcDirTest
            // Only copy the test code
            include "**/*Test.h"
            include "**/*Test.m"
        })
    }
}
