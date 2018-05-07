import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.validation.constraints.NotNull
/**
 * This task will create a new version in Jira and assign it to the correct component
 * It expects to get an oldVersion in the form "{version} {component}" for example
 * "10 web" and will create the following integer version against the same component.
 *
 * It will also mark the existing version as released.
 */
public class JiraVersionCreate extends DefaultTask {

    @Input @NotNull
    String oldVersion

    @Input @NotNull
    String projectCode

    @TaskAction
    def void run() {
        def (thisVersion, componentName) = oldVersion.split(' ')
        def nextVersion = thisVersion.toInteger() + 1

        // find the component id
        def componentRecord = getJiraResponse("api/2/project/${projectCode}/components", null).find { it.name == componentName }
        if (!componentRecord) {
            logger.error("Could not find component with name ${componentName}")
        }
        logger.info "Found existing component id ${componentRecord.id}"

        // find the existing version id
        def oldVersionRecord = getJiraResponse("api/2/project/${projectCode}/versions", null).find { it.name == oldVersion }
        logger.info "Found existing version id ${oldVersionRecord.id}"

        // create a new version
        def versionData = JsonOutput.toJson([
            name: "${nextVersion} ${componentName}",
            archived: false,
            released: false,
            project: projectCode
        ])
        def newVersion = getJiraResponse('api/2/version', versionData)
        logger.info "Created new version ${newVersion.id}"

        // link to the new version to the correct component id
        def componentData = JsonOutput.toJson([
            "componentId": componentRecord.id,
            "versionId": newVersion.id,
            "released": false,
        ])
        getJiraResponse('com.deniz.jira.mapping/latest/',componentData)
        logger.info "Created mapping to component"

        // mark the old version as released
        def versionRelease = JsonOutput.toJson([
                releaseDate: new Date().format('yyyy-MM-dd'),
                released: true
        ])
        getJiraResponse("api/2/version/${oldVersionRecord.id}", versionRelease, "PUT")
        logger.info "Updated old version and set it to released."
    }

    def getJiraResponse(query, data, action="POST") {
        // https://developer.atlassian.com/cloud/jira/platform/rest/
        def SQUISH_URL = "https://squish.ish.com.au/rest/"
        def jiraUsername = "gradle"
        def jiraPassword = "7Am1*^X8A#ul"
        def encoded_auth = "${jiraUsername}:${jiraPassword}".getBytes().encodeBase64().toString()

        def connection = (SQUISH_URL + query).toURL().openConnection()
        logger.lifecycle "Getting " + connection.getURL()
        logger.debug "Sending data " + new JsonBuilder(data).toPrettyString()

        connection.addRequestProperty("Authorization", "Basic ${encoded_auth}")
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("Accept", "application/json")

        if (data) {
            connection.setRequestMethod(action)
            connection.setDoOutput(true)

            def writer = new OutputStreamWriter(connection.outputStream)
            writer.write(data.toString())
            writer.flush()
            writer.close()
        } else {
            connection.setRequestMethod("GET")
        }
        connection.connect()

        def slurper = new JsonSlurper()
        def response = slurper.parseText(connection.content.text)
        logger.info new JsonBuilder(response).toPrettyString()

        return response
    }
}