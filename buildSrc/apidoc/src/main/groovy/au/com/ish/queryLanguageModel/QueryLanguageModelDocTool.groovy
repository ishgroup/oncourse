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

package au.com.ish.queryLanguageModel

import groovy.text.GStringTemplateEngine
import groovy.text.Template
import groovy.util.slurpersupport.GPathResult
import groovyjarjarantlr.RecognitionException
import groovyjarjarantlr.TokenStreamException
import org.codehaus.groovy.groovydoc.GroovyClassDoc
import org.codehaus.groovy.groovydoc.GroovyRootDoc
import org.codehaus.groovy.tools.groovydoc.FileOutputTool
import org.codehaus.groovy.tools.groovydoc.OutputTool
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

import java.util.stream.Collectors

class QueryLanguageModelDocTool {
    private final String ANGEL_MAP_PATH = "../server/src/main/resources/cayenne/AngelMap.map.xml"
    private final String SYNTHETIC_ATTRIBUTES_PATH = "syntheticAttributes.xml"
    private final String IGNORED_ATTRIBUTES_PATH = "ignoredAttributes.xml"
    private final String RICHTEXT_ATTRIBUTES_PATH = "richTextAttributes.xml"
    private final String OUTPUT_FILE = "/queryLanguageModel.ts"

    private static final String WILLOW_ID_PROPRETY = 'willowId'
    private static final String RICHTEXT_ATTRIBUTE_TYPE = 'RichText'
    private static final STRING_CLASSES = ['string', 'java.lang.String', 'byte[]']
    private static final DATE_CLASSES = ['java.util.Date', 'java.time.LocalDate', 'java.time.LocalDateTime']
    private static final NUMERIC_CLASSES = ['int', 'java.lang.Integer', 'java.lang.Long', 'ish.math.Money', 'java.lang.Double', 'java.math.BigDecimal', 'float']

    /**
     * SPECIFIC_ENTITIES specifies some cayenne entities which will be generated except rule - "don't generate entities that have super entities"
     */
    private static final List<String> SPECIFIC_ENTITIES = Arrays.asList("Invoice", "SaleOrder", "ArticleProduct", "Article", "MembershipProduct", "Membership", "VoucherProduct", "Voucher")

    private final QueryLanguageModelDocBuilder rootDocBuilder
    private final GStringTemplateEngine engine = new GStringTemplateEngine()
    private final OutputTool output = new FileOutputTool()

    private Template docTemplate
    def project
    private Logger logger
    private static Set<String> enumsSet = new HashSet()

    QueryLanguageModelDocTool(File docTemplateURL, Project project) {

        this.project = project
        this.logger = project.logger
        this.rootDocBuilder = new QueryLanguageModelDocBuilder()

        docTemplate = engine.createTemplate(docTemplateURL)
    }


    void renderToOutput(File output) throws Exception {
        GroovyRootDoc rootDoc = rootDocBuilder.resolve()
        output.write(generateInterfaces())

        def enums = rootDoc.classes().findAll { enumsSet.contains(it.fullPathName.replaceAll("/", "."))}
        output.append(generateEnums(enums))
    }

    /**
     * Adding the files to the tool causes them to be parsed by the antlr based groovy or java parser
     *
     * @param files
     * @throws groovyjarjarantlr.RecognitionException
     * @throws groovyjarjarantlr.TokenStreamException
     * @throws IOException
     */
    void add(FileCollection files) throws RecognitionException, TokenStreamException, IOException {
        rootDocBuilder.setOverview()

        for (File srcFile : files) {
            if (srcFile.exists()) {
                rootDocBuilder.processFile(srcFile)
            }
        }
    }


    private String generateEnums(List<GroovyClassDoc> enums) throws Exception {
        Map<String, Object> binding = new HashMap<String, Object>()
        binding.put("enums", enums)
        def output = new StringBuilder()
        output.append("export class Enum {\n  constructor(props) { \n   Object.assign(this, props); \n  }\n}\n\n")
        output.append(docTemplate.make(binding).toString())
        return output
    }


    private String generateInterfaces() {
        def output = new StringBuilder()
        File angelMapFile = project.file(ANGEL_MAP_PATH)
        String text = angelMapFile.text
        GPathResult list = new XmlSlurper().parseText(text)
        List entities = list['obj-entity'].collect()
        List dbRelations = list['db-relationship'].collect()
        List relations = list['obj-relationship'].collect()

        InputStream syntheticAttributesFile = getClass().getResourceAsStream(SYNTHETIC_ATTRIBUTES_PATH)
        GPathResult synthAttributes = new XmlSlurper().parseText(syntheticAttributesFile.text)
        List entitiesWithSynthAttributes = synthAttributes['entity'].collect()

        InputStream ignoredAttributesFile = getClass().getResourceAsStream(IGNORED_ATTRIBUTES_PATH)
        GPathResult ignoredAttributes = new XmlSlurper().parseText(ignoredAttributesFile.text)
        List entitiesWithIgnoredAttributes = ignoredAttributes['entity'].collect()

        InputStream richTextAttributesFile = getClass().getResourceAsStream(RICHTEXT_ATTRIBUTES_PATH)
        GPathResult richTextAttributes = new XmlSlurper().parseText(richTextAttributesFile.text)
        List entitiesWithRichTextAttributes = richTextAttributes['entity'].collect()

        entities
            .findAll { it.@superEntityName.isEmpty() || SPECIFIC_ENTITIES.contains(it.@name) }
            .each {
                logger.log(LogLevel.INFO, "Generating typescript interface for " + it.@name.toString())
                output.append("export const ${it.@name} = {\n")
                GPathResult entity = it as GPathResult
                GPathResult ignoredAttrs = entitiesWithIgnoredAttributes.find {it.@name == entity.@name} as GPathResult
                GPathResult richTextAttrs = entitiesWithRichTextAttributes.find {it.@name == entity.@name} as GPathResult
                output.append(generateProperties( it['obj-attribute'].collect().toList(),
                        ignoredAttrs , richTextAttrs))
                output.append(generateRelations( relations, dbRelations, entities, entity,
                        ignoredAttrs ))
                output.append(generateSyntheticAttributes( entitiesWithSynthAttributes, entity ))

                // render parent's fields
                if (SPECIFIC_ENTITIES.contains(it.@name)) {
                    generateParentsProperties(entities, dbRelations,
                            relations,it as GPathResult, entitiesWithSynthAttributes,
                            output, entitiesWithIgnoredAttributes)
                }

                output.append("};\n\n")
            }
        return output
    }

    /**
     * Recursively generates parent's properties for entity
     * @param entities which was read from datamap
     * @param dbRelations which was read from datamap
     * @param relations which was read from datamap
     * @param currentNode - current GPathResult node which is analyzing
     * @param renderedSrc - output for result string
     */
    private static void generateParentsProperties(List entities, List dbRelations,
                                                  List relations, GPathResult currentNode,
                                                  List entitiesWithSynthAttributes, StringBuilder renderedSrc,
                                                  List ignoredAttributes) {
        GPathResult parentEntity = entities.find{ entity -> entity.@name.equals(currentNode.@superEntityName) }
        if (parentEntity != null) {
            GPathResult ignoredAttrsForEntity = ignoredAttributes.find {it.@name == parentEntity.@name}
            renderedSrc.append(generateProperties(parentEntity['obj-attribute'].collect().toList(),
                    ignoredAttrsForEntity, null))
            renderedSrc.append(generateRelations(relations, dbRelations, entities, parentEntity,
                    ignoredAttrsForEntity))
            renderedSrc.append(generateSyntheticAttributes( entitiesWithSynthAttributes, parentEntity ))
            generateParentsProperties(entities, dbRelations, relations,
                    parentEntity, entitiesWithSynthAttributes,
                    renderedSrc, ignoredAttributes)
        }
    }


    private static StringBuilder generateProperties(List properties, GPathResult ignoredAttributes, GPathResult richTextAttributes) {
        StringBuilder renderedProperties = new StringBuilder()

        List ignored = ignoredAttributes ?
                ignoredAttributes['attribute'].collect()
                        .stream()
                        .map{it -> it.@name.toString() }
                        .collect(Collectors.toList()) as List :
                Collections.emptyList() as List
        List richText = richTextAttributes ?
                richTextAttributes['attribute'].collect()
                        .stream()
                        .map{it -> it.@name.toString() }
                        .collect(Collectors.toList()) as List :
                Collections.emptyList() as List

        properties
            .findAll { it.@name != WILLOW_ID_PROPRETY && !ignored.contains(it.@name.toString())}
            .each  {
                if (richText.contains(it.@name.toString())) {
                    it.@type = RICHTEXT_ATTRIBUTE_TYPE
                }
            }
            .each { renderedProperties.append("   ${it.@name}: '${defineTypescriptReturnType(it.@type.toString())}',\n") }

        renderedProperties
    }


    private static StringBuilder generateRelations(List objRelations,
                                                   List dbRelations,
                                                   List entities,
                                                   GPathResult entity,
                                                   GPathResult ignoredAttributes) {
        StringBuilder renderedRelationProperties = new StringBuilder()
        List relationships = ignoredAttributes ?
                ignoredAttributes['relationship'].collect()
                        .stream()
                        .map{it -> it.@name.toString() }
                        .collect(Collectors.toList()) :
                Collections.emptyList()
        objRelations
            .findAll { it.@source == entity.@name && !relationships.contains(it.@name.toString())}
            .each { objRel ->

                if (dbRelations.any { dbRel -> dbRel.@name == objRel.@'db-relationship-path' && dbRel.@source == entity.@dbEntityName }) {
                    GPathResult targetEntity = entities.find { it.@name == objRel.@target }
                    String returnType = targetEntity.@superEntityName.isEmpty() ? targetEntity.@name : targetEntity.@superEntityName

                        renderedRelationProperties.append("   ${objRel.@name}: '${returnType}',\n")
                    }
                }

        renderedRelationProperties
    }

    private static StringBuilder generateSyntheticAttributes(List entitiesWithSynthAttributes, GPathResult entity) {
        StringBuilder renderedSyntheticAttributes = new StringBuilder()

        GPathResult entityWithAttr = entitiesWithSynthAttributes.find {it.@name == entity.@name}
        if(entityWithAttr) {
            List attributes = entityWithAttr['attribute'].collect()
            attributes.each { attr ->
                renderedSyntheticAttributes.append("   ${attr.@name}: '${defineTypescriptReturnType(attr.@ref.toString())}',\n")
            }
        }

        renderedSyntheticAttributes
    }

    private static String defineTypescriptReturnType(String typeName) {
        def result
        switch (typeName) {
            case ['java.lang.Boolean', 'boolean']:
                result = 'Boolean'
                break
            case STRING_CLASSES:
                result = 'String'
                break
            case DATE_CLASSES:
                result = 'Date'
                break
            case NUMERIC_CLASSES:
                result = 'Number'
                break
            case 'java.util.TimeZone':
                result = 'any'
                break
            case ~/ish\..*/:
                result = typeName.split("\\.").last()
                enumsSet.add(typeName)
                break
            default:
                result = typeName
                break
        }

        result
    }
}
