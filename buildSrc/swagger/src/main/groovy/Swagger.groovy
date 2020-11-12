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

import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@CacheableTask
class Swagger extends DefaultTask {
    Swagger() {
        group = "build"
    }

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File resourcesJava = project.file("${project.parent.projectDir}/buildSrc/swagger/src/main/resources/swaggerTemplates/java")

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File resourcesJS = project.file("${project.parent.projectDir}/buildSrc/swagger/src/main/resources/swaggerTemplates/js")

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    File ignoreFiles = project.file("${project.parent.projectDir}/buildSrc/swagger/src/main/resources/.swagger-codegen-ignore")

    @InputFile
    @PathSensitive(PathSensitivity.RELATIVE)
    File schema

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File sourceFolder

    @Input
    Integer schemaVersion

    @OutputDirectory
    File javaOutput

    @OutputDirectory
    File jsOutput

    @OutputDirectory
    File traitOutput

    @TaskAction
    void run() {
        def configJava = new CodegenConfigurator()
        configJava.setInputSpec(schema.path)
        configJava.setOutputDir(javaOutput.path)
        configJava.setLang('jaxrs-cxf')
        configJava.setIgnoreFileOverride(ignoreFiles.path)
        configJava.setAdditionalProperties([
                'templateDir':  resourcesJava.path,
                'sourceFolder': 'src/main/groovy',
                'implFolder': 'src/main/groovy',
                'useBeanValidation': false,
                'modelPackage'  : "ish.oncourse.server.api.v${schemaVersion}.model".toString(),
                'apiPackage'    : "ish.oncourse.server.api.v${schemaVersion}.service".toString(),
                'supportingFiles': '', // skip scripts and maven files
                'appVersion'    : project.version
        ])
        configJava.setModelNameSuffix('DTO')

        def opt = configJava.toClientOptInput()
        opt.config.modelTemplateFiles.put('model.mustache', '.groovy')
        opt.config.apiTemplateFiles.put('apiServiceImpl.mustache', '.groovy')
        opt.config.importMapping.put("LocalDate", "java.time.LocalDate")
        opt.config.importMapping.put("LocalDateTime", "java.time.LocalDateTime")
        opt.config.typeMapping.put("DateTime", "LocalDateTime")
        opt.config.typeMapping.put("Date", "LocalDate")

        def generator = new DefaultGenerator()
        generator.setGeneratorPropertyDefault('apiTests', 'false')
        generator.setGeneratorPropertyDefault('modelTests', 'false')
        generator.opts(opt).generate()

        /*****/
        /* Write DTO trait classes */
        /*****/
        generator.swagger.getDefinitions().each { name, model ->
            def traitFile = project.file("${traitOutput.path}/${opt.config.toModelName(name)}Trait.groovy")
            if (traitFile.createNewFile()) {
                traitFile.text = """package ish.oncourse.server.api.traits

trait ${name}DTOTrait {

}"""
                print("File created at ${traitFile.path}")
            }
        }
        /*****/
        /* Javascript */
        /*****/
        def configJS = new CodegenConfigurator()
        configJS.setInputSpec(schema.path)
        configJS.setOutputDir(jsOutput.path)
        configJS.setLang('io.swagger.codegen.languages.TypeScriptExtendedClientCodegen')
        configJS.setAdditionalProperties([
                'templateDir':  resourcesJS.path,
                'supportingFiles': '', // skip scripts and maven files
                'withXml'       : true,
                'appVersion'    : project.version
        ])

        def tsOpt = configJS.toClientOptInput()

        tsOpt.config.typeMapping.put("DateTime", "string")
        tsOpt.config.typeMapping.put("any", "any")
        tsOpt.config.typeMapping.put("Date", "string")
        tsOpt.config.typeMapping.put("date", "string")

        def generate = new DefaultGenerator().opts(tsOpt).generate()
        for (File file : generate) {
            postProcessGeneratedTypeScript(file)
        }
    }

    private static void postProcessGeneratedTypeScript(File file) {
        Path path = Paths.get(file.getAbsolutePath())
        println(path)
        Charset charset = StandardCharsets.UTF_8

        // Map MIME types to javascript applicable types
        Map<String, String> mappingFormats = new HashMap<>()
        mappingFormats.put("application/pdf", "blob")
        mappingFormats.put("application/xml", "blob")
        mappingFormats.put("application/csv", "blob")
        mappingFormats.put("application/zip", "blob")
        mappingFormats.put("application/gzip", "blob")
        mappingFormats.put("application/json", "")
        mappingFormats.put("text/html", "")

        String text = new String(Files.readAllBytes(path), charset)
        for (Map.Entry<String, String> entry : mappingFormats) {
            text = text.replace(entry.key, entry.value)
        }
        Files.write(path, text.getBytes(charset))
    }

}
