/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.generator.chapter


import au.com.ish.docs.generator.DSLGenerator
import au.com.ish.docs.helpers.CollectionHelper
import au.com.ish.docs.helpers.DocHelper
import au.com.ish.docs.helpers.RenderHelper
import au.com.ish.docs.helpers.StringHelper
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.slf4j.LoggerFactory

import static au.com.ish.docs.utils.GroovyDocUtils.getIsVisible

class ChapterDSLGenerator implements DSLGenerator {

    private def LOGGER = LoggerFactory.getLogger(ChapterDSLGenerator.class)

    protected Handlebars generator

    ChapterDSLGenerator() {
        generator = buildGenerator()
    }

    @Override
    String generate(Collection<GroovyClassDoc> classes, String templateName) throws Exception {
        //todo
        Template template = generator.compile('/au/com/ish/docs/templates/chapter')

        for (GroovyClassDoc classDoc : classes ) {
            LOGGER.debug("Generating DSL documentation for " + classDoc.simpleTypeName())
            def binding = new HashMap<String, Object>() {{
                put('classDoc', classDoc)
                put('visibleMethods', getVisibleMethods.call(classDoc))
                put('visibleConstructors', getVisibleConstructors.call(classDoc))
                put('distDir', templateName)
            }}
            return template.apply(binding)
        }

        return StringUtils.EMPTY
    }

    private static getVisibleMethods = { doc ->
        (doc.methods().findAll(isVisible) + doc.superclass()?.methods()?.findAll(isVisible)).findAll()
    }

    private static getVisibleConstructors = { doc ->
        doc.constructors().findAll(isVisible)
    }

    protected Handlebars buildGenerator() {
        Handlebars handlebars = new Handlebars() {{
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
        }}
        return handlebars
    }

}
