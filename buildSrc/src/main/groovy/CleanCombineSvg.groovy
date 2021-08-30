import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class CleanCombineSvg extends DefaultTask {

    @Input
    def String imgDirectory

    @TaskAction
    def void cleanCombineSvg() {
        def imgDirectory = new File(imgDirectory)
        imgDirectory.deleteDir()
    }
}
