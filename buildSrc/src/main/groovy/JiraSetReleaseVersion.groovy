import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.validation.constraints.NotNull

/**
 * Get the next available version of this component from Jira and put it into the variable project.ext.releaseVersion
 */
class JiraSetReleaseVersion extends DefaultTask {
    @Input @NotNull
    String componentName

    @Input @NotNull
    String projectCode

    @TaskAction
    def void run() {
        // find the component id
        def componentRecord = JiraVersionCreate.getJiraResponse("api/2/project/${projectCode}/components", null).find { it.name == componentName }
        if (!componentRecord) {
            logger.error("Could not find component with name ${componentName}")
        }
        logger.info("${componentRecord}")

        // find the existing versions
        def lastVersion = JiraVersionCreate.getJiraResponse("com.deniz.jira.mapping/latest/applicable_versions?selectedComponentIds=${componentRecord.id}",
                null).findAll { !it.isReleased && it.id > 0 }.sort { it.name }.last()
        logger.info "Found existing version ${lastVersion}"

        def nextVersion = lastVersion.name.split().first().toInteger()

        project.setProperty('releaseVersion', nextVersion)
        project.version = nextVersion

        logger.lifecycle("Release version set:${nextVersion}") // don't change this string since it is detected by buildbot
    }
}
