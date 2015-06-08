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
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
/**
 *
 */
class J2objcAssembleTask extends DefaultTask {

    // Generated ObjC source files
    @InputDirectory
    File srcGenDir

    // Generated ObjC binaries
    @InputDirectory
    File libDir

    @OutputDirectory
    File getDestSrcDir() {
        return project.file(destSrcDirPath)
    }

    @OutputDirectory
    File getDestSrcDirTest() {
        return project.file(destSrcDirTestPath)
    }

    @OutputDirectory
    File getDestLibDir() {
        return project.file(destLibDirPath)
    }

    // j2objcConfig dependencies for UP-TO-DATE checks

    // We keep these strings as @Input properties in addition to the @OutputDirectory methods above because,
    // for example, whether or not the main source and test source are identical affects execution of this task.
    @Input
    String getDestSrcDirPath() { return project.j2objcConfig.destSrcDir }

    @Input
    String getDestSrcDirTestPath() { return project.j2objcConfig.destSrcDirTest }

    @Input
    String getDestLibDirPath() { return project.j2objcConfig.destLibDir }


    private def clearDestSrcDirWithChecks(File destDir, String name) {
        def nonObjcDestFiles = project.files(project.fileTree(
                dir: destDir, excludes: ["**/*.h", "**/*.m"]))
        // Warn if deleting non-generated objc files from destDir
        nonObjcDestFiles.each { file ->
            def message =
                    "Unexpected files in $name - this folder should contain ONLY j2objc\n" +
                    "generated files Objective-C. The folder contents are deleted to remove\n" +
                    "files that are nolonger generated. Please check the directory and remove\n" +
                    "any files that don't end with Objective-C extensions '.h' and '.m'.\n" +
                    "$name: ${destDir.path}\n" +
                    "Unexpected file for deletion: ${file.path}"
            throw new InvalidUserDataException(message)
        }
        // TODO: better if this was a sync operation as it does deletes automatically
        logger.debug "Deleting $name to fill with generated objc files... " + destDir.path
        project.delete destDir
    }

    @TaskAction
    def destCopy() {
        clearDestSrcDirWithChecks(destSrcDir, 'destSrcDir')
        project.copy {
            includeEmptyDirs = false
            from srcGenDir
            into destSrcDir
            // TODO: this isn't precise, main source can be suffixed with Test as well.
            // Would be best to somehow keep the metadata about whether a file was from the
            // main sourceset or the test sourceset.
            // Don't copy the test code
            exclude "**/*Test.h"
            exclude "**/*Test.m"
        }

        if (destSrcDirTest.absolutePath != destSrcDir.absolutePath) {
            // If we want main source and test source in one directory, then don't
            // re-delete the main directory where we just put files!
            clearDestSrcDirWithChecks(destSrcDirTest, 'destSrcDirTest')
        }
        project.copy {
            includeEmptyDirs = false
            from srcGenDir
            into destSrcDirTest
            // Only copy the test code
            include "**/*Test.h"
            include "**/*Test.m"
        }

        // We don't need to clear out the library path, our libraries can co-exist
        // with other libraries if the user wishes them to.
        project.copy {
            includeEmptyDirs = true
            from libDir
            into destLibDir
            include "**/*.a"
        }
    }
}