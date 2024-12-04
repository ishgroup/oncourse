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

package au.com.ish.docs

import org.codehaus.groovy.tools.groovydoc.LinkArgument
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory

class DslDocsPlugin implements Plugin<Project> {

    private static final String PLUGIN_NAME = 'dslDocs'

    void apply(Project project) {

        Configuration configuration = new Configuration(project.extensions.create(PLUGIN_NAME, DslDocsPluginExtension))

        project.configurations.create(PLUGIN_NAME)
                .setVisible(false)
                .setTransitive(true)
                .setDescription('The apiDocs libraries to be used for this project.')

        project.task(PLUGIN_NAME) {
            doLast {
                def javaLink = new LinkArgument()
                javaLink.setPackages("java.,org.xml.,javax.,org.xml.")
                javaLink.setHref("http://download.oracle.com/javase/11/docs/api")

                def groovyLink = new LinkArgument()
                groovyLink.setPackages("groovy.,org.codehaus.groovy.")
                groovyLink.setHref("http://groovy.codehaus.org/api")

                DslGroovyDocTool docTool = new DslGroovyDocTool([groovyLink, javaLink].toList(), project)
                docTool.cleanUpDirectory(configuration.distationDir)
                docTool.add(configuration.sourceFiles)
                docTool.renderToOutput(configuration.distationDir)
            }
        }
    }
}

class DslDocsPluginExtension {

    @InputFiles
    FileCollection source

    @OutputDirectory
    File destinationDir
}
