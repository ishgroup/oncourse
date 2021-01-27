import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class Swagger extends DefaultTask {
    Swagger() {
        group = "build"
    }

    @Input
    File resourcesJava = new File("${project.parent.projectDir}/buildSrc/src/main/resources/swaggerTemplates/java")

    @Input
    File resourcesJS = new File("${project.parent.projectDir}/buildSrc/src/main/resources/swaggerTemplates/js")

    @Input
    File schema

    @Input
    String apiPackage
    
    @Input
    String dtoPackage
    
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
                'templateDir':  resourcesJava.path,
                'sourceFolder': 'src/main/groovy',
                'implFolder': 'src/main/groovy',
                'useBeanValidation': false,
                'modelPackage'  : dtoPackage,
                'apiPackage'    : apiPackage,
                'supportingFiles': '', // skip scripts and maven files
                'appVersion'    : project.version,
                'apiTestTemplateFiles': ['api_test.mustache':'.groovy']
        ])

        def opt = configJava.toClientOptInput()
        opt.config.apiTemplateFiles.put('apiServiceImpl.mustache', '.groovy')
        opt.config.importMapping.put("LocalDateTime", "java.time.LocalDateTime")
        opt.config.typeMapping.put("DateTime", "LocalDateTime")
        
        def generator = new DefaultGenerator()
        generator.setGeneratorPropertyDefault('apiTests', 'false')
        generator.setGeneratorPropertyDefault('modelTests', 'false')
        generator.opts(opt).generate()


        def configJS = new CodegenConfigurator()
        configJS.setInputSpec(schema.path)
        configJS.setOutputDir(jsOutput.path)
        configJS.setLang('typescript-fetch')
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

//        tsOpt.config.apiPackage = "js.http"
//        tsOpt.config.modelPackage = "js.model"

        new DefaultGenerator().opts(tsOpt).generate()
    }
}