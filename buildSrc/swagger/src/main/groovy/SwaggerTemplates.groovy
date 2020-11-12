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
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

/**
 * Print to the console a list of all the mustache template variables available
 */
class SwaggerTemplates extends DefaultTask {

    SwaggerTemplates() {
        group = "help"
    }

    @InputFile
    File schema

    @Input
    String language

    @TaskAction
    void run() {
        def config = new CodegenConfigurator()
        config.setInputSpec(schema.path)
        config.setLang(language)
        System.setProperty("debugOperations", "true")
        new DefaultGenerator().opts(config.toClientOptInput()).generate()
        System.clearProperty("debugOperations")
    }

}
