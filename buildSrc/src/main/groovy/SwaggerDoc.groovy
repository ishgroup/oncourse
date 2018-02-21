import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class SwaggerDoc extends DefaultTask {
    @Input
    File schema

    @OutputDirectory
    File docOutput

    @TaskAction
    void swaggerDoc() {
        def config = new CodegenConfigurator()
        config.setInputSpec(schema.path)
        config.setOutputDir(docOutput.path)
        config.setLang('dynamic-html')
        new DefaultGenerator().opts(config.toClientOptInput()).generate()

//            main = 'io.swagger.codegen.SwaggerCodegen'
//            args  = [ 'generate',
//                      '-i', swaggerSchema,
//                      '-l', 'dynamic-html',
//                      '-t', "$swaggerResources",
//                      '-o', outputs.files.last()
//            ]
//        }
//
//        task docCss (type: Copy, dependsOn: docGenerate) {
//            from "${swaggerResources}"
//            into "${buildDir}/doc/docs/assets/css"
//        }
    }

}
