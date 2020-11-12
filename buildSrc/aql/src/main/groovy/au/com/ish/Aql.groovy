/*
 * Copyright ish group pty ltd 2020.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 */

package au.com.ish

import org.antlr.v4.Tool
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

@CacheableTask
class Aql extends DefaultTask {

    Aql() {
        group = 'build'
    }

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    File queryGrammar = new File("${project.parent.projectDir}/buildSrc/aql/src/main/resources/Aql.g4")

    @Input
    String target

    @Optional
    @Input
    String packageDir

    @OutputDirectory
    File outputDir

    @TaskAction
    void run() {
        logger.lifecycle("Generate $target query grammar into $outputDir.path")

        String[] args = [queryGrammar.path,
                         "-Dlanguage=$target",
                         '-o', outputDir.path
        ]

        if (packageDir != null) {
            args += ['-package', packageDir]
        }

        new Tool(args).processGrammarsOnCommandLine()
    }
}
