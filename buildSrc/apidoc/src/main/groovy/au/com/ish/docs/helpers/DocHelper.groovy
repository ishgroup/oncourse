/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers

import au.com.ish.docs.generator.chapter.ChapterContext
import au.com.ish.docs.generator.root.SectionContext
import au.com.ish.docs.utils.GroovyDocUtils
import au.com.ish.docs.utils.TextUtils
import com.github.jknack.handlebars.Options
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType
import org.codehaus.groovy.tools.groovydoc.*

import static au.com.ish.docs.Configuration.*
/**
 * The {@code DocHelper} class provides utility methods designed to assist in generating and formatting
 * Groovy classes, methodes and parameteres for DSLs documentation specifically for use within Handlebars template generators.
 *
 * <p>
 * The `DocHelper` class includes various helper
 * </p>
 *
 * <h5>Key Helper Methods:</h5>
 * <ul>
 *   <li>{@code callMethod} - Dynamically calls a method on an object using the method name as a parameter.</li>
 *   <li>{@code inMethod} - Checks if a given object is present in a collection.</li>
 *   <li>{@code docName} - Modifies and formats documentation names for better readability.</li>
 *   <li>{@code splitClasses} - Categorizes classes into enums, entities, and others for easier rendering.</li>
 *   <li>{@code linkable} - Generates Markdown-style links for Groovy types, supporting DSL documentation links.</li>
 *   <li>{@code paramDoc}, {@code returnDoc}, {@code methodDoc} - Extracts and formats specific parts of documentation.</li>
 * </ul>
 */
class DocHelper {

    private static List<String> collectionTypes = [Collection.simpleName, Collection.name]
    private static List<String> listTypes = [List.name, List.simpleName, ArrayList.name, ArrayList.simpleName, LinkedList.name, LinkedList.simpleName, Vector.name, Vector.simpleName, Stack.name, Stack.simpleName]
    private static List<String> setTypes = [Set.name, Set.simpleName, HashSet.name, HashSet.simpleName, LinkedHashSet.name, LinkedHashSet.simpleName, TreeSet.name, TreeSet.simpleName, EnumSet.name, EnumSet.simpleName]
    private static List<String> queueTypes = [Queue.name, Queue.simpleName, PriorityQueue.name, PriorityQueue.simpleName, LinkedList.name, LinkedList.simpleName]
    private static List<String> dequeTypes = [Deque.name, Deque.simpleName, ArrayDeque.name, ArrayDeque.simpleName]
    private static List<String> mapTypes = [Map.name, Map.simpleName, HashMap.name, HashMap.simpleName, LinkedHashMap.name, LinkedHashMap.simpleName, TreeMap.name, TreeMap.simpleName, WeakHashMap.name, WeakHashMap.simpleName, IdentityHashMap.name, IdentityHashMap.simpleName, EnumMap.name, EnumMap.simpleName]

    /**
     * Helpder method used to invoke a method on an object.
     *
     * <p>
     *     Handlebars templates do natively allow to get access to field values, but not to call object methods.
     *     This helper method provides a way to dynamically call a method on the provided object
     *     using the method name passed as a string.
     * </p>
     *
     * <h5>Usage:</h5>
     * <p>
     *     To use this helper in a template, it must first be registered
     *     in a {@link com.github.jknack.handlebars.Handlebars} generator.
     * </p>
     *
     * <h5>Parameters:</h5>
     * <ul>
     *   <li>{@code obj} - The object on which the method should be invoked.</li>
     *   <li>{@code options} - The {@link Options} object containing generator context and parameters passed to the helper.
     *       The first parameter should be the name of the method to invoke.</li>
     * </ul>
     *
     * <h5>Example:</h5>
     * {@code {{call classDoc "name"}} }
     */
    @Helper
    def static callMethod(Object obj, Options options) {
        def _method = options.param(0) as String
        return obj."${_method}"()
    }

    /**
     * Helpder method used to check if a given object is present in a collection.
     *
     * <h5>Usage:</h5>
     * <p>
     *     To use this helper in a template, it must first be registered
     *     in a {@link com.github.jknack.handlebars.Handlebars} generator.
     * </p>
     *
     * <h5>Parameters:</h5>
     * <ul>
     *   <li>{@code obj} - The object to check for presence in the list.</li>
     *   <li>{@code options} - The {@link Options} object containing list in which the object should be;
     *   also containing other parameters passed to the helper </li>
     * </ul>
     *
     * <h5>Returns:</h5>
     * <p>A boolean value: {@code true} if the object is present in the list, {@code false} otherwise.</p>
     *
     * <h5>Example:</h5>
     * {@code (in (call (call this "returnType") "typeName")}
     */
    @Helper
    def static inMethod(Object obj, Options options) {
        return options.params.contains(obj)
    }

    @Helper
    def static docName(SimpleGroovyAbstractableElementDoc doc, Options options) {
        String name = doc.name()
        if (name.endsWith("Spec")) {
            return name.replace("Spec", " script block")
        }
        if (name.endsWith("Spec")) {
            return name.replace("ScriptClosure", " script block")
        }
        return name
    }

    @Helper
    def static methodDoc(String text, Options options) {
        def result = cleanupDoc(text)
        result = result.split(/@param\s/).first()
        result = result.split(/@return\s/).first()
        result = TextUtils.trimBoundaryNewlines(result).trim()
        return  result
    }

    @Helper
    def static paramDoc(String paramName, Options options) {
        String text = options.param(0) as String
        def split = cleanupDoc(text).split(/@param\s+${paramName}/)
        if (split.size() != 2) {
            return "*Documentation not yet available*"
        }
        return split.last().replaceAll('\n+', ' ')
    }

    @Helper
    def static returnDoc(String text, Options options) {
        def split = cleanupDoc(text).split('@return')
        if (split.size() != 2) {
            return "*Documentation not yet available*"
        }
        return split.last().replaceAll('\n', ' ')
    }

    private static cleanupDoc(String text) {
        if (StringUtils.isEmpty(text)) {
            return ''
        }
        // remove left '*' remanants of the javadoc notation
        def result = text.stripMargin("*").stripIndent()
        // convert javadoc code notation to DSL notaion
        result = result.replaceAll(~/\{@code\s+.+?}/, {
            return (it as String).replaceFirst(~/\{@code\s+/, "`").replaceFirst(~/}$/, "`").toString()
        })
        return result
    }

    @Helper
    def static getNullability(GroovyMethodDoc element, Options options) {
        return element.annotations().find { ann ->
            if (ann.name() == NULLABLE_ANNONATION) {
                return '<span class="nullable">Nullable</span>'
            } else {
                if (element.annotations().find { _ann -> _ann.name() == NOTNULL_ANNONATION }) {
                    return '<span class="nullable">Not null</span>'
                }
            }
            return ""
        }
    }

    /**
     * Helper method that splits a collection of classes into categories for rendering in templates.
     * The method divides the input collection into three groups:
     * enums, entities, and other non-enum, non-entity classes including scripts and closures.
     */
    @Helper
    def static splitClasses(Collection<SimpleGroovyClassDoc> classes, Options options) {
        def enums = classes.findAll { cls -> cls.isEnum() }
        def nonEnums = classes.findAll { cls -> !cls.isEnum() }
        def entities = nonEnums.findAll { cls ->
            cls.parentClasses.any { parent -> GroovyDocUtils.isCayenneEntity(cls) }
        }
        def other = nonEnums.findAll { cls ->
            !cls.parentClasses.any { parent -> GroovyDocUtils.isCayenneEntity(cls) }
        }
        return options.fn([enums: enums, entities: entities, other: other])
    }


    /**
     * Helper method that generates link of a given Groovy type  in markdown style.
     *
     * <p>
     *     This method processes a type and returns either a simple type name or a formatted link to the
     *     type's DSL documentation based on certain conditions such as type hierarchy, sourse, generics, or external types.
     * </p>
     *
     * <h5>Usage:</h5>
     * <p>
     *     To use this helper in a template, it must first be registered
     *     in a {@link com.github.jknack.handlebars.Handlebars} generator.
     * </p>
     *
     * <h5>Parameters:</h5>
     * <ul>
     *   <li>{@code type} - The {@link GroovyType} whose linkable representation is to be generated.</li>
     *   <li>{@code options} - The {@link Options} object containing generator context and parameters passed to the helper.</li>
     * </ul>
     *
     * <h5>Returns:</h5>
     * <p>A string representing the type that can be:
     * <ul>
     *   <li>simple type name for Java classes or classes that not included in DSL documentation</li>
     *   <li>`"NULL TYPE???"` label for undefiend types</li>
     *   <li>link to a markdown file stored type DSL documentation</li>
     * </ul>
     *
     * <h5>Example:</h5>
     * {@code {{linkable (call this "type")}} }
     */
    @Helper
    static def linkable(GroovyType type, Options options) {

        if (!type) return "NULL TYPE???"

        def simpleTypeName = type.simpleTypeName()
        if (type instanceof SimpleGroovyType || type.isPrimitive()) {
            return simpleTypeName
        }
        if (type instanceof ExternalGroovyClassDoc || simpleTypeName in ['BigDecimal', 'def', 'PersistentObject'] || simpleTypeName?.startsWith('java')) {
            return "${simpleTypeName.tokenize('.').last()}"
        }

        def generics = simpleTypeName.tokenize('<>,')
        if (generics.first()) {
            def genericType = generics.first().split('extends ').last()
            def argumentType = generics.last().split('extends ').last()

            if (genericType in collectionTypes || genericType in listTypes || genericType in setTypes || genericType in queueTypes || genericType in dequeTypes) {
                def type2 = "${genericType} of ${computeTypeLink(argumentType, options)}"
                return type2
            }
            if (genericType in mapTypes) {
                def keyArgumentType = generics[1].split('extends ').last()
                def type2 = "${genericType} of ${computeTypeLink(keyArgumentType, options)}, ${computeTypeLink(argumentType, options)}"
                return type2
            }
            if (genericType in [Class.name, Class.simpleName]) {
                def type2 = "${genericType} extends ${computeTypeLink(argumentType, options)}"
                return type2
            }
        }

        return computeTypeLink(type.simpleTypeName(), options)
    }

    private static  GroovyClassDoc findType(String plainType, Collection<GroovyClassDoc> allDocs) {
        return allDocs.find { it.fullPathName == plainType || it.simpleTypeName() == plainType }
    }

    private static boolean isDSLDocumentation(String docPath, Collection<GroovyClassDoc> allDocs) {
        return allDocs.find { it.fullPathName == docPath }
    }

    private static String computeTypeLink(String type, Options options) {
        def allDocs = (options.context.data("root") as SectionContext).classes
        def _type = findType(type, allDocs)
        return _type ? computeTypeLink(_type, options) : formatTypeLink(type)
    }

    private static String computeTypeLink(GroovyClassDoc type, Options options) {
        def allDocs = (options.context.data("root") as SectionContext).classes
        String prefix = ""

        if (!isDSLDocumentation(type.fullPathName, allDocs)) {
            return formatTypeLink(type.simpleTypeName())
        }

        GroovyClassDoc classDoc = options.context.model() instanceof SimpleGroovyMethodDoc ?
                SimpleGroovyMethodDocWrapper.valueOf(options.context.model() as SimpleGroovyMethodDoc).getBelongsToClass() :
                (options.context?.parent()?.parent()?.model() as ChapterContext).classDoc

        if (classDoc != null) {
            def docFullPath = classDoc.fullPathName
            def typeFullPath = type.fullPathName
            prefix = TextUtils.computeRelativePath(docFullPath, typeFullPath)
        }
        return formatTypeLink(type.simpleTypeName(), prefix)
    }

    private static String formatTypeLink(String name, String pathPrefix = '') {
        return !pathPrefix.isEmpty() ? "[${name}](${pathPrefix}${name}.${RESULT_DOC_TYPE})" : name
    }

}
