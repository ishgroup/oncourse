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
import org.slf4j.LoggerFactory

import static au.com.ish.docs.utils.FileUtils.getTemplateSourceRootPath

/**
 * Implementation of the Handlebars generator for generating DSL documentation sections.
 * This class uses a specified Handlebars template to render documentation for a given {@link SectionContext},
 * leveraging the context's project-specific details and predefined helpers.
 *
 * <p> To find example of class usage, see {@link au.com.ish.docs.helpers.FileHelper} </p>
 *
 * <h5>Dependencies:</h5>
 * <ul>
 *   <li>{@link Handlebars} - Template engine for processing section templates.</li>
 *   <li>{@link DocHelper}, {@link FileHelper}, {@link CollectionHelper} - Helper classes for content processing and rendering.</li>
 *   <li>{@link SectionContext} -  Provides information required for generating a documentation section.</li>
 * </ul>
 *
 * @see DSLGenerator
 * @see SectionContext
 */
class SectionDSLGenerator implements DSLGenerator<SectionContext> {

    private static final LOGGER = LoggerFactory.getLogger(SectionDSLGenerator.class)

    protected String templateName

    protected Handlebars generator

    SectionDSLGenerator(String templateName) {
        this.templateName = templateName
        this.generator = buildGenerator()
    }

    @Override
    String generate(SectionContext context) throws Exception {
        LOGGER.debug("Generating doc index")
        String path = getTemplateSourceRootPath(templateName, context.project)
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
