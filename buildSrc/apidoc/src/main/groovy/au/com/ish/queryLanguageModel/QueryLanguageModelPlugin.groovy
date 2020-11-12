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

package au.com.ish.queryLanguageModel


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory

class QueryLanguageModelPlugin implements Plugin<Project> {

    def EXTENSION_NAME = 'generateQueryLanguageModel'
    def logger

    void apply(Project project) {

        logger = project.logger

        def DOC_TEMPLATE = project.file("../buildSrc/apidoc/src/main/resources/au/com/ish/queryLanguageModel/queryLanguageModelTemplate.js")

        project.configurations.create('generateQueryLanguageModel')
            .setVisible(false)
            .setTransitive(true)
            .setDescription('The ReactInterfaces Ant libraries to be used for this project.')

        QueryLanguageModelPluginExtension queryLanguageModelPluginExtension = project.extensions.create(EXTENSION_NAME, QueryLanguageModelPluginExtension)

        Task generateQueryLanguageModelTask = project.task('generateQueryLanguageModel') {
            doLast {
                QueryLanguageModelDocTool docTool = new QueryLanguageModelDocTool(DOC_TEMPLATE, project)
                FileCollection sourceFiles = queryLanguageModelPluginExtension.source
                    .filter{ it.absolutePath.endsWith(".java") || it.absolutePath.endsWith(".groovy")}

                docTool.add(sourceFiles)

                def output = new File(queryLanguageModelPluginExtension.destinationDir, "queryLanguageModel.ts")
                output.parentFile.mkdirs()
                docTool.renderToOutput(output)
            }
        }
    }
}

class QueryLanguageModelPluginExtension {

    @InputFiles
    FileCollection source

    @OutputDirectory
    File destinationDir
}

