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

    def logger

    void apply(Project project) {

        logger = project.logger

        def TEMPLATE_BASEDIR = "au/com/ish/docs/templates/"
        def DOC_TEMPLATE = project.file("../buildSrc/apidoc/src/main/resources/" + TEMPLATE_BASEDIR + "index.adoc")
        def CLASS_TEMPLATE = project.file("../buildSrc/apidoc/src/main/resources/" + TEMPLATE_BASEDIR + "chapter.adoc")

        project.configurations.create('dslDocs')
                .setVisible(false)
                .setTransitive(true)
                .setDescription('The apiDocs Ant libraries to be used for this project.')

        DslDocsPluginExtension dslDocsPluginExtension = project.extensions.create('dslDocs', DslDocsPluginExtension)

        project.task('dslDocs') {
            doLast {

                def javaLink = new LinkArgument()
                javaLink.setPackages("java.,org.xml.,javax.,org.xml.")
                javaLink.setHref("http://download.oracle.com/javase/8/docs/api")

                def groovyLink = new LinkArgument()
                groovyLink.setPackages("groovy.,org.codehaus.groovy.")
                groovyLink.setHref("http://groovy.codehaus.org/api")

                // by filtering out all files which don't contain @API before trying to parse them this runs much faster
                // we also need to grab _ class files in order to get the Cayenne superclasses
                FileCollection sourceFiles = dslDocsPluginExtension.source.
                    filter{ it.absolutePath.endsWith(".java") || it.absolutePath.endsWith(".groovy")}.
                    filter{ it.text.contains('@API') || (it.absolutePath.contains("ish/oncourse/server/cayenne")
                            && (it.text.contains('public class ') || it.text.contains('public abstract class')))}
                DslGroovyDocTool docTool = new DslGroovyDocTool(
                        DOC_TEMPLATE,
                        CLASS_TEMPLATE,
                        [groovyLink, javaLink].toList(),
                        project
                )

                docTool.add(sourceFiles)
                docTool.renderToOutput(dslDocsPluginExtension.destinationDir.canonicalPath)

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
