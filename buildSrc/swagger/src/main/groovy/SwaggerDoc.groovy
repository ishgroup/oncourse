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

import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
class SwaggerDoc extends DefaultTask {

    SwaggerDoc() {
        group = "documentation"
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File resourcePath = new File("${project.parent.projectDir}/buildSrc/swagger/src/main/resources/swaggerTemplates/html")

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    File schema

    @OutputDirectory
    File docOutput

    @TaskAction
    void run() {
        def config = new CodegenConfigurator()
        config.setInputSpec(schema.path)
        config.setOutputDir(docOutput.path)
        config.setLang('dynamic-html')
        config.setAdditionalProperties([
                'templateDir':  resourcePath.path
                ])
        new DefaultGenerator().opts(config.toClientOptInput()).generate()

        project.copy {
            from resourcePath.path + "/assets/css"
            into "${docOutput.path}/docs/assets/css"
        }
    }

}
