/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.generator.root


import au.com.ish.docs.generator.DSLGenerator
import au.com.ish.docs.helpers.CollectionHelper
import au.com.ish.docs.helpers.DocHelper
import au.com.ish.docs.helpers.FileHelper
import com.github.jknack.handlebars.Handlebars
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.OutputTool
import org.gradle.api.Project
import org.slf4j.LoggerFactory

import static au.com.ish.docs.utils.FileUtils.convertPathToPackage
import static au.com.ish.docs.utils.FileUtils.getTemplateSourceRootPath

class SectionDSLGenerator implements DSLGenerator {

    private static final LOGGER = LoggerFactory.getLogger(SectionDSLGenerator.class)

    protected OutputTool output
    protected String distributionDirectory
    protected Project project

    protected Handlebars generator

    SectionDSLGenerator(OutputTool output, Project project, String distributionDirectory) {
        this.output = output
        this.distributionDirectory = distributionDirectory
        this.project = project
        this.generator = buildGenerator()
    }

    @Override
    String generate(Collection<GroovyClassDoc> classes, String templateName) throws Exception {
        LOGGER.debug("Generating doc index")
        String path = getTemplateSourceRootPath(templateName, project)
        def context = new Context.Builder()
                .setTemplateLocation(convertPathToPackage(path))
                .setClasses(classes)
                .setDistDir(distributionDirectory)
                .setProject(project)
                .setOutput(output)
                .setProject(project)
                .build()

        return generator.compile(path).apply(context)
    }

    protected Handlebars buildGenerator() {
        Handlebars handlebars = new Handlebars() {
            {
                registerHelper("splitClasses", DocHelper.&splitClasses)
                registerHelper("sort", CollectionHelper.&sort)
                registerHelper("render", FileHelper.&render)
                registerHelper("renderChapter", FileHelper.&renderChapter)
            }
        }
        return handlebars
    }
}
