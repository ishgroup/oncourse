import io.swagger.codegen.DefaultGenerator
import io.swagger.codegen.config.CodegenConfigurator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Print to the console a list of all the mustache template variables available
 */
class SwaggerTemplates extends DefaultTask {

    SwaggerTemplates() {
        group = "help"
    }

    @Input
    File schema

    @Input
    String language

    @TaskAction
    void run() {
        def config = new CodegenConfigurator()
        config.setInputSpec(schema.path)
        config.setLang(language)
        System.setProperty("debugOperations", "true")
        new DefaultGenerator().opts(config.toClientOptInput()).generate()
        System.clearProperty("debugOperations")
    }

}
