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
    void run() {
        def config = new CodegenConfigurator()
        config.setInputSpec(schema.path)
        config.setOutputDir(docOutput.path)
        config.setLang('dynamic-html')
        new DefaultGenerator().opts(config.toClientOptInput()).generate()

        project.copy {
            from "${project.parent.projectDir}/buildSrc/src/main/resources"
            include "style.css"
            into "${docOutput.path}/docs/assets/css"
        }
    }

}
