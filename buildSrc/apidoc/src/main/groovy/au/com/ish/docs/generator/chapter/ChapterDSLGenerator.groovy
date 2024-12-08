/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.generator.chapter

import au.com.ish.docs.Configuration
import au.com.ish.docs.generator.DSLGenerator
import au.com.ish.docs.generator.root.SectionContext
import au.com.ish.docs.helpers.CollectionHelper
import au.com.ish.docs.helpers.DocHelper
import au.com.ish.docs.helpers.RenderHelper
import au.com.ish.docs.helpers.StringHelper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import org.slf4j.LoggerFactory

/**
 * Implementation of the Handlebars generator for creating DSL documentation for classes annotated with {@code @API}
 * according to the `chapter.md` template.
 *
 * <p> To find example of class usage, see {@link au.com.ish.docs.helpers.FileHelper} </p>
 *
 * <h5>Dependencies:</h5>
 * <ul>
 *   <li>{@link Handlebars} - Template engine for processing the chapter template.</li>
 *   <li>Helper classes:
 *     <ul>
 *       <li>{@link DocHelper}</li>
 *       <li>{@link RenderHelper}</li>
 *       <li>{@link StringHelper}</li>
 *       <li>{@link CollectionHelper}</li>
 *     </ul>
 *   </li>
 *   <li>{@link SectionContext} - Provides the information required for rendering templates.</li>
 * </ul>
 *
 * @see DSLGenerator
 * @see ChapterContext
 */

class ChapterDSLGenerator implements DSLGenerator<ChapterContext> {

    private def LOGGER = LoggerFactory.getLogger(ChapterDSLGenerator.class)

    protected SectionContext templateContext
    protected Handlebars generator

    ChapterDSLGenerator(SectionContext templateContext) {
        this.templateContext = templateContext
        generator = buildGenerator()
    }

    @Override
    String generate(ChapterContext context) throws Exception {
        Template template = generator.compile(Configuration.CHAPTER_TEMPLATE)
        LOGGER.debug("Generating DSL documentation for " + context.classDoc.simpleTypeName())
        return template.apply(context)
    }

    protected Handlebars buildGenerator() {
        Handlebars handlebars = new Handlebars() {
            {
                registerHelper("docName", DocHelper.&docName)
                registerHelper("methodDoc", DocHelper.&methodDoc)
                registerHelper("linkable", DocHelper.&linkable)
                registerHelper("paramDoc", DocHelper.&paramDoc)
                registerHelper("returnDoc", DocHelper.&returnDoc)
                registerHelper("getNullability", DocHelper.&getNullability)
                registerHelper("sort", CollectionHelper.&sort)
                registerHelper("removePrefix", StringHelper.&removePrefix)
                registerHelper("capitalize", StringHelper.&capitalize)
                registerHelper("trim", StringHelper.&trim)
                registerHelper("call", DocHelper.&callMethod)
                registerHelper("in", DocHelper.&inMethod)
                registerHelper("newLine", RenderHelper.&getNewLineOuput)
                registerHelper("space", RenderHelper.&getSpace)
                registerHelper("isEmpty", StringHelper.&isEmpty)
            }
        }
        return handlebars
    }

}
