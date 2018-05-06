import groovy.json.JsonBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.validation.constraints.NotNull

public class JiraVersionCreate extends DefaultTask {

    @Input @NotNull
    String oldVersion

    @Input @NotNull
    String jiraUsername

    @Input @NotNull
    String jiraPassword

    @TaskAction
    def void run() {
        def (thisVersion, componentName) = oldVersion.split(' ')
        def nextVersion = thisVersion.toInteger() + 1

        def component = getJiraResponse('api/2/project/OD/components', null).find { it.name == componentName }
        if (!component) {
            logger.error("Could not find component with name ${componentName}")
        }
        def versionData = JsonOutput.toJson([
            name: "${nextVersion} ${componentName}",
            archived: false,
            released: false,
            project: "OD"
        ])
        def newVersion = getJiraResponse('api/2/version', versionData)

        def componentData = JsonOutput.toJson([
            "componentId": component.id,
            "versionId": newVersion.id,
            "released": false,
        ])
        getJiraResponse('com.deniz.jira.mapping/latest/',componentData)
    }

    def getJiraResponse(query, data) {
        if (!jiraPassword || !jiraUsername) {
            logger.error("Please put jiraPassword and jiraUsername properties into ~/.gradle/gradle.properties")
        }
        // https://developer.atlassian.com/cloud/jira/platform/rest/
        def SQUISH_URL = "https://squish.ish.com.au/rest/"
        def encoded_auth = "${jiraUsername}:${jiraPassword}".getBytes().encodeBase64().toString()

        def connection = (SQUISH_URL + query).toURL().openConnection()
        logger.lifecycle "Getting " + connection.getURL()
        connection.addRequestProperty("Authorization", "Basic ${encoded_auth}")
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("Accept", "application/json")

        if (data) {
            connection.setRequestMethod("POST")
            connection.setDoOutput(true)

            def writer = new OutputStreamWriter(connection.outputStream)
            writer.write(data.toString())
            writer.flush()
            writer.close()
        } else {
            connection.setRequestMethod("GET")
        }

        logger.info(connection.toString())
        connection.connect()

        def slurper = new JsonSlurper()
        def json_data = slurper.parseText(connection.content.text)
        logger.info new JsonBuilder(json_data).toPrettyString()

        return json_data
    }
}