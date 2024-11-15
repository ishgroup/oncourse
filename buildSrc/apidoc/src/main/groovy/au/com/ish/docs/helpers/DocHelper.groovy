/*
 * Copyright ish group pty ltd 2024.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License version 3 as published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
 */

package au.com.ish.docs.helpers

import au.com.ish.docs.utils.TextUtils
import com.github.jknack.handlebars.Options
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyMethodDoc
import org.codehaus.groovy.groovydoc.GroovyType
import org.codehaus.groovy.tools.groovydoc.ExternalGroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyAbstractableElementDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyType

import static au.com.ish.docs.utils.GroovyDocUtils.isCayenneEntity

class DocHelper {

    private static List<String> collectionTypes = [Collection.simpleName, Collection.name]
    private static List<String> listTypes = [List.name, List.simpleName, ArrayList.name, ArrayList.simpleName, LinkedList.name, LinkedList.simpleName, Vector.name, Vector.simpleName, Stack.name, Stack.simpleName]
    private static List<String> setTypes = [Set.name, Set.simpleName, HashSet.name, HashSet.simpleName, LinkedHashSet.name, LinkedHashSet.simpleName, TreeSet.name, TreeSet.simpleName, EnumSet.name, EnumSet.simpleName]
    private static List<String> queueTypes = [Queue.name, Queue.simpleName, PriorityQueue.name, PriorityQueue.simpleName, LinkedList.name, LinkedList.simpleName]
    private static List<String> dequeTypes = [Deque.name, Deque.simpleName, ArrayDeque.name, ArrayDeque.simpleName]
    private static List<String> mapTypes = [Map.name, Map.simpleName, HashMap.name, HashMap.simpleName, LinkedHashMap.name, LinkedHashMap.simpleName, TreeMap.name, TreeMap.simpleName, WeakHashMap.name, WeakHashMap.simpleName, IdentityHashMap.name, IdentityHashMap.simpleName, EnumMap.name, EnumMap.simpleName]

    @Helper
    def static callMethod(Object obj, Options options) {
        def _method = options.param(0) as String
        return obj."${_method}"()
    }

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

    @Helper
    private static cleanupDoc(String text) {
        // remove left '*' remanants of the javadoc notation
        def result = text.stripMargin("*").stripIndent()
        // convert javadoc code notation to DSL notaion
        result = result.replaceAll(~/\{@code\s+.+?}/, {
            return (it as String).replaceFirst(~/\{@code\s+/, "`").replaceFirst(~/}$/, "`").toString()
        })
        return result
    }

    @Helper
    static def linkable(GroovyType type, Options options) {

        if (!type) return "NULL TYPE???"

        def simpleTypeName = type.simpleTypeName()
        if (type instanceof SimpleGroovyType || type.isPrimitive()) {
            return type.simpleTypeName()
        }
        if (type instanceof ExternalGroovyClassDoc || simpleTypeName in ['BigDecimal', 'def', 'PersistentObject'] || simpleTypeName?.startsWith('java')) {
            return "${simpleTypeName.tokenize('.').last()}"
        }

        def generics = simpleTypeName.tokenize('<>,')
        String pathPrefix = computePathPrefix(type, options)
        if (generics.first()) {
            def genericType = generics.first().split('extends ').last()
            def argumentType = generics.last().split('extends ').last()

            if (genericType in collectionTypes || genericType in listTypes || genericType in setTypes || genericType in queueTypes || genericType in dequeTypes) {
                return "${genericType} of [${generics.last()}](${pathPrefix}${argumentType}.md)"
            }
            if (genericType in mapTypes) {
                // если это simple type то не нужна ссылка на метод
                def keyArgumentType = generics[1].split('extends ').last()
                return "${genericType} of [${generics[1]}](${pathPrefix}${keyArgumentType}.md), [${generics.last()}](${pathPrefix}${argumentType}.md)"
            }
            if (genericType in [Class.name, Class.simpleName]) {
                return "${genericType} extends [${generics.last()}](${pathPrefix}${argumentType}.md)"
            }
        }

        return "[${simpleTypeName}](${pathPrefix}${simpleTypeName}.md)"
    }

    private static String computePathPrefix(GroovyType groovyType, Options options) {
        String prefix = ""
        def classDocContext = options.context?.parent()?.parent()?.model()
        if (classDocContext != null) {
            def classDoc = (classDocContext as HashMap<String, Object>).get("classDoc") as GroovyClassDoc
            def docFullPath = classDoc.fullPathName
            def typeFullPath = (groovyType as GroovyClassDoc).fullPathName
            prefix = TextUtils.computeRelativePath(docFullPath, typeFullPath)
        }
        return prefix.isEmpty() ? "./" : prefix
    }

    @Helper
    def static getNullability(GroovyMethodDoc element, Options options) {
        return element.annotations().find { ann ->
            if (ann.name() == "javax/annotation/Nullable") {
                return '<span class="nullable">Nullable</span>'
            } else {
                if (element.annotations().find { _ann -> _ann.name() == "javax/annotation/Nonnull" }) {
                    return '<span class="nullable">Not null</span>'
                }
            }
            return ""
        }
    }

    @Helper
    def static splitClasses(Collection<SimpleGroovyClassDoc> classes, Options options) {
        def enums = classes.findAll { cls -> cls.isEnum() }
        def nonEnums = classes.findAll { cls -> !cls.isEnum() }
        def entities = nonEnums.findAll { cls ->
            cls.parentClasses.any { parent -> isCayenneEntity(cls) }
        }
        def other = nonEnums.findAll { cls ->
            !cls.parentClasses.any { parent -> isCayenneEntity(cls) }
        }
        return options.fn([enums: enums, entities: entities, other: other])
    }

}
