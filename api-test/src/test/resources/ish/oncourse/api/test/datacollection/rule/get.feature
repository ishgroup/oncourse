@parallel=false
Feature: Main feature for all GET requests with path '/datacollection/rule'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/rule'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


    Scenario: (+) Get all datacollection rules
    
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2
        
        * match response[*].name contains only ["Non-accredited course", "Accredited course"]
        * match response[*].enrolmentFormName contains only ["Non-accredited course enrolment form (Enrolment)", "Accredited course enrolment form (Enrolment)"]
        * match each response[*].applicationFormName == "Application form (Application)"
        * match each response[*].waitingListFormName == "Waiting list form (Waiting List)"
        * match each response[*].payerFormName == null
        * match each response[*].parentFormName == null
        * match each response[*].surveyForms == '#[0]'