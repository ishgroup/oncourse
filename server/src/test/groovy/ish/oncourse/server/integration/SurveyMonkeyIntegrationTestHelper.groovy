package ish.oncourse.server.integration

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import ish.oncourse.server.integration.surveymonkey.SurveyMonkeyIntegration

@CompileDynamic
class SurveyMonkeyIntegrationTestHelper {

    static String surveyName = 'test '
    static String apiKey = ''
    static String authToken = 'lg-t0as9gFSIo5u-IBAF0Ms41LhWJLrVSbrj8NEHMtW9NhuvwliVYEficY.yZYwS.9Jf38ce-8gg6K0yyGVadjadyRYcCiKsaX8OVRltAXZTeEOQO.q7lBb1vteiGYDw'

    static String subject = 'Survey invitation'
    static String emailBody = 'Please complete a short survey: [SurveyLink]\n' +
            'To unsubscribe please click: [OptOutLink]\n' +
            '\n' +
            '\n' +
            'Powered by SurveyMonkey'

    static String replyTo = 'test@mail'

    static String email = 'aeqweqwewqeqwe@gmail.com'
    static String firstName = 'testFirstName'
    static String lastName = 'testLastName'
    static String customId = '33'


    
    static void main(String[] args) {
        def surveyMonkey = new SurveyMonkeyIntegration(
                apiKey: apiKey,
                authToken: authToken,
                surveyName: surveyName)


        def getSurveyListResponse = surveyMonkey.getSurveyId(surveyName)
        String survey_id = getSurveyListResponse.data[0]?.id
        println "survey_id = ${survey_id}"


        def getCollectorListResponse = surveyMonkey.getCollectorList(survey_id)
        def allEmailCollectors = getCollectorListResponse.data?.findAll { c ->
            c.status == 'open' && c.type == 'email'
        }
        //find collector_id with name 'onCourse'
        def collector_id = allEmailCollectors?.find { c ->
            c.name == 'onCourse'
        }?.id
        if (!collector_id) {
            collector_id = allEmailCollectors[0]?.id
        }

        println "collector_id = ${collector_id}"


        def messageResponse = surveyMonkey.getMessage(collector_id)

        String messageId = messageResponse.id

        println "message_id = ${messageId}"


        def recipientsResponse = surveyMonkey.getRecipient(collector_id, messageId, email, firstName, lastName)

        def recId = recipientsResponse.id

        println "recipient_id = ${recId}"

        def send = surveyMonkey.send(collector_id, messageId)
        println send
    }
}
