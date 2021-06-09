@parallel=false
Feature: Main feature for all DELETE requests with path 'datacollection/form'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        * def ishPathForm = 'datacollection/form'
        * def ishPathRule = 'datacollection/rule'



    Scenario: (+) Delete existing (not system) DataCollectionForm

#       Prepare new DataCollectionForm to delete it
#       <--->
        * def someDataCollectionForm = {"name":"Survey#9","type":"Survey","fields":[],"headings":[{"name":"Heading#1","description":"Description#1"}],"deliverySchedule":"On demand"}

        Given path ishPathForm
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPathForm
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 20
        And match response[*].name contains 'Survey#9'

        * def id = get[0] response[?(@.name == 'Survey#9')].id

        * call read('../../removeEntityById.feature') {path: '#(ishPathForm)', entityId: '#(id)'}

        Given path ishPathForm
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 19
        And match response[*].name !contains 'Survey#9'


    Scenario: (-) Delete existing (system) DataCollectionForm

        Given path ishPathForm + '/-1'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Default configuration can't be deleted."


    Scenario: (-) Delete not existing DataCollectionForm
        Given path ishPathForm + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form 100000 is not exist"


    Scenario: (-) Delete DataCollectionForm without ID
        Given path ishPathForm
        When method DELETE
        Then status 405


    Scenario: (-) Delete DataCollectionForm with null ID
        Given path ishPathForm + '/null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Form is not exist"


    Scenario: (-) Delete existing (used for DataCollectionRule) DataCollectionForm

#       Prepare new DataCollectionForms which are used in DataCollectionRules
#       <--->
        * def someDataCollectionFormArray =
        """
        [
        {"name":"Enrolment#123","type":"Enrolment","fields":[],"headings":[]},
        {"name":"Application#123","type":"Application","fields":[],"headings":[]},
        {"name":"WaitingList#123","type":"WaitingList","fields":[],"headings":[]},
        {"name":"Survey#123","type":"Survey","fields":[],"headings":[],"deliverySchedule":"On enrol"},
        {"name":"Payer#123","type":"Payer","fields":[{"type":{"uniqueKey":"suburb","label":"Suburb"},"label":"Suburb","helpText":"123","mandatory":true},{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"123","mandatory":true},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"123","mandatory":true}],"headings":[],"deliverySchedule":null},
        {"name":"Parent#123","type":"Parent","fields":[{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"123","mandatory":true},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"123","mandatory":true},{"type":{"uniqueKey":"state","label":"State"},"label":"State","helpText":"123","mandatory":true}],"headings":[],"deliverySchedule":null}
        ]
        """

        Given path ishPathForm
        And request someDataCollectionFormArray[0]
        When method POST
        Then status 204

        Given path ishPathForm
        And request someDataCollectionFormArray[1]
        When method POST
        Then status 204

        Given path ishPathForm
        And request someDataCollectionFormArray[2]
        When method POST
        Then status 204

        Given path ishPathForm
        And request someDataCollectionFormArray[3]
        When method POST
        Then status 204

        Given path ishPathForm
        And request someDataCollectionFormArray[4]
        When method POST
        Then status 204

        Given path ishPathForm
        And request someDataCollectionFormArray[5]
        When method POST
        Then status 204

        Given path ishPathForm
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 26
        And match response[*].name contains ['Enrolment#123', 'Application#123', 'WaitingList#123', 'Survey#123', 'Payer#123', 'Parent#123']

        * def someDataCollectionRuleArray =
        """
        [
        {"name":"someRule#1","enrolmentFormName":"Enrolment#123","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"},
        {"name":"someRule#2","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Application#123","waitingListFormName":"Accredited course enrolment form (Waiting List)"},
        {"name":"someRule#3","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"WaitingList#123"},
        {"name":"someRule#4","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","surveyForms":["Survey#123"]},
        {"name":"someRule#5","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","payerFormName":"Payer#123"},
        {"name":"someRule#6","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","parentFormName":"Parent#123"}
        ]
        """

        Given path ishPathRule
        And request someDataCollectionRuleArray[0]
        When method POST
        Then status 204

        Given path ishPathRule
        And request someDataCollectionRuleArray[1]
        When method POST
        Then status 204

        Given path ishPathRule
        And request someDataCollectionRuleArray[2]
        When method POST
        Then status 204

        Given path ishPathRule
        And request someDataCollectionRuleArray[3]
        When method POST
        Then status 204

        Given path ishPathRule
        And request someDataCollectionRuleArray[4]
        When method POST
        Then status 204

        Given path ishPathRule
        And request someDataCollectionRuleArray[5]
        When method POST
        Then status 204

        Given path ishPathRule
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 8
        And match response[*].name contains ['someRule#1', 'someRule#2', 'someRule#3', 'someRule#4', 'someRule#5', 'someRule#6']
#       <--->

        Given path ishPathForm
        When method GET
        Then status 200

        * def id1 = get[0] response[?(@.name == 'Enrolment#123')].id
        * def id2 = get[0] response[?(@.name == 'Application#123')].id
        * def id3 = get[0] response[?(@.name == 'WaitingList#123')].id
        * def id4 = get[0] response[?(@.name == 'Survey#123')].id
        * def id5 = get[0] response[?(@.name == 'Payer#123')].id
        * def id6 = get[0] response[?(@.name == 'Parent#123')].id

        Given path ishPathForm + '/' + id1
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form can not be deleted, used for data collection rule"

        Given path ishPathForm + '/' + id2
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form can not be deleted, used for data collection rule"

        Given path ishPathForm + '/' + id3
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form can not be deleted, used for data collection rule"

        Given path ishPathForm + '/' + id4
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form can not be deleted, used for data collection rule"

        Given path ishPathForm + '/' + id5
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form can not be deleted, used for data collection rule"

        Given path ishPathForm + '/' + id6
        When method DELETE
        Then status 400
        And match response.errorMessage == "The data collection form can not be deleted, used for data collection rule"

#       Scenario have been finished. Now find and remove created objects from DB
#       <--->

        * call read('../../removeEntity.feature') {path: '#(ishPathRule)', entityName: 'someRule#1'}
        * call read('../../removeEntity.feature') {path: '#(ishPathRule)', entityName: 'someRule#2'}
        * call read('../../removeEntity.feature') {path: '#(ishPathRule)', entityName: 'someRule#3'}
        * call read('../../removeEntity.feature') {path: '#(ishPathRule)', entityName: 'someRule#4'}
        * call read('../../removeEntity.feature') {path: '#(ishPathRule)', entityName: 'someRule#5'}
        * call read('../../removeEntity.feature') {path: '#(ishPathRule)', entityName: 'someRule#6'}

        Given path ishPathRule
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2
        And match response[*].name !contains ['someRule#1', 'someRule#2', 'someRule#3', 'someRule#4', 'someRule#5', 'someRule#6']

        * call read('../../removeEntity.feature') {path: '#(ishPathForm)', entityName: 'Enrolment#123'}
        * call read('../../removeEntity.feature') {path: '#(ishPathForm)', entityName: 'Application#123'}
        * call read('../../removeEntity.feature') {path: '#(ishPathForm)', entityName: 'WaitingList#123'}
        * call read('../../removeEntity.feature') {path: '#(ishPathForm)', entityName: 'Survey#123'}
        * call read('../../removeEntity.feature') {path: '#(ishPathForm)', entityName: 'Payer#123'}
        * call read('../../removeEntity.feature') {path: '#(ishPathForm)', entityName: 'Parent#123'}

        Given path ishPathForm
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains ['Enrolment#123', 'Application#123', 'WaitingList#123', 'Survey#123', 'Payer#123', 'Parent#123']
#       <--->