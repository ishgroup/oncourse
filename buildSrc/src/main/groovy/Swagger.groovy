import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class Swagger extends DefaultTask {
    @Input
    File schema

    @Input
    Integer schemaVersion

    @OutputDirectory
    File javaOutput

    @OutputDirectory
    File jsOutput

    @TaskAction
    void run() {
        def configJava = new CodegenConfigurator()
        configJava.setInputSpec(schema.path)
        configJava.setOutputDir(javaOutput.path)
        configJava.setLang('jaxrs-cxf')
        configJava.setIgnoreFileOverride("${project.parent.projectDir}/buildSrc/src/main/resources/.swagger-codegen-ignore".toString())
        configJava.setAdditionalProperties([
                'templateDir':  "${project.parent.projectDir}/buildSrc/src/main/resources/swaggerTemplates".toString(),
                'sourceFolder': 'src/main/groovy',
                'implFolder': 'src/main/groovy',
                'useBeanValidation': false,
                'modelPackage'  : "ish.oncourse.willow.editor.v${schemaVersion}.model".toString(),
                'apiPackage'    : "ish.oncourse.willow.editor.v${schemaVersion}.service".toString(),
                'supportingFiles': '', // skip scripts and maven files
                'dateLibrary'   : 'java8',
                'appVersion'    : project.version,
                'apiTestTemplateFiles': ['api_test.mustache':'.groovy']
        ])

        def opt = configJava.toClientOptInput()
        opt.config.apiTestTemplateFiles.clear()
        opt.config.apiTestTemplateFiles.put('api_test.mustache', '.groovy')
        opt.config.testFolder =  'src/test/groovy'
        opt.config.apiTemplateFiles.put('apiServiceImpl.mustache', '.groovy')
        new DefaultGenerator().opts(opt).generate()


        def configJS = new CodegenConfigurator()
        configJS.setInputSpec(schema.path)
        configJS.setOutputDir(jsOutput.path)
        configJS.setLang('typescript-fetch')
        configJS.setAdditionalProperties([
                'supportingFiles': '', // skip scripts and maven files
                'withXml'       : true,
                'appVersion'    : project.version
        ])
        new DefaultGenerator().opts(configJS.toClientOptInput()).generate()
    }
}