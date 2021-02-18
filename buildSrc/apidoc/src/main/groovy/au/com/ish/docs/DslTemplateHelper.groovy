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

import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType
import org.codehaus.groovy.tools.groovydoc.ExternalGroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyType
/**
 * Some helper methods for the templates
 */
class DslTemplateHelper {

    def log
    DslGroovyDocTool tool

    DslTemplateHelper(DslGroovyDocTool tool) {
        this.tool = tool
        this.log = tool.project.logger
    }

    def linkable(GroovyType type) {
        if (!type) {
            return "NULL TYPE???"
        }
        log.debug(type.simpleTypeName())

        if (type instanceof SimpleGroovyType || type.isPrimitive()) {
            return type.simpleTypeName()
        }
        def generics = type.simpleTypeName().tokenize('<>,')
        if (generics.first() in ["List", "Collection", "Set"]) {
            return "${generics.first()} of <<${generics.last()}>>"
        }

        if (generics.first() in ["Map"]) {
            return "${generics.first()} of ${generics[1]}, ${generics.last()}"
        }

        if (generics.first() in ["Class"]) {
            return "${generics.first()} extends <<${generics.last().split('extends ').last()}>>"
        }

        if (type instanceof ExternalGroovyClassDoc || type.simpleTypeName() in ['BigDecimal', 'def', 'PersistentObject'] || type.simpleTypeName()?.startsWith('java')) {
            return "${type.simpleTypeName().tokenize('.').last()}"
        }

        return "<<${type.simpleTypeName()}>>"
    }

    def static methodName(String name) {
        if (name.endsWith("Spec")) {
            return name.replace("Spec", " script block")
        }
        if (name.endsWith("Spec")) {
            return name.replace("ScriptClosure", " script block")
        }
        return name
    }

    def static getNullability = { GroovyMethodDoc element ->
        element.annotations().find { ann -> ann.name() == "javax/annotation/Nullable" } ? "[.nullable]#Nullable#" :
                element.annotations().find { ann -> ann.name() == "javax/annotation/Nonnull" } ? "[.nullable]#Not null#" : ""
    }

    def static paramDoc(String paramName, String text) {
        def split = cleanupDoc(text).split(/@param\s+${paramName}/)
        if (split.size() != 2) {
            return "_Documentation not yet available_"
        }
        return split.last().replaceAll('\n', ' ')

    }

    def static returnDoc(String text) {
        def split = cleanupDoc(text).split('@return')
        if (split.size() != 2) {
            return "_Documentation not yet available_"
        }
        return split.last().replaceAll('\n', ' ')
    }

    def static methodDoc(String text) {
        def result = cleanupDoc(text)
        result = result.split(/@param\s/).first()
        result = result.split(/@return\s/).first()

        return result
    }

    private static cleanupDoc(String text) {
        // remove left '*' remanants of the javadoc notation
        def result = text.stripMargin("*").stripIndent()

        result = result.replaceAll("</?code>", "`")

        return result
    }
}
