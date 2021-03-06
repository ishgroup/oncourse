@parallel=false
Feature: Main feature for all POST requests with path '/datacollection/rule'
    
    
    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/rule'
        * def datacollectionForm = 'datacollection/form'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Create datacollection rule

#       Prepare new 'Survey', 'Payer', 'Parent' DataCollectionForms for creating a new DataCollectionRule
#       <--->
        * def someDataCollectionFormArray =
        """
        [
        {"name":"Survey#1","type":"Survey","fields":[],"headings":[],"deliverySchedule":"On enrol"},
        {"name":"Survey#2","type":"Survey","fields":[],"headings":[],"deliverySchedule":"On enrol"},
        {"name":"Payer#1","type":"Payer","fields":[{"type":{"uniqueKey":"suburb","label":"Suburb"},"label":"Suburb","helpText":"123","mandatory":true},{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"123","mandatory":true},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"123","mandatory":true}],"headings":[],"deliverySchedule":null},
        {"name":"Parent#1","type":"Parent","fields":[{"type":{"uniqueKey":"street","label":"Street"},"label":"Street","helpText":"123","mandatory":true},{"type":{"uniqueKey":"country","label":"Country"},"label":"Country","helpText":"123","mandatory":true},{"type":{"uniqueKey":"state","label":"State"},"label":"State","helpText":"123","mandatory":true}],"headings":[],"deliverySchedule":null}
        ]
        """

        Given path 'datacollection/form'
        And request someDataCollectionFormArray[0]
        When method POST
        Then status 204

        Given path 'datacollection/form'
        And request someDataCollectionFormArray[1]
        When method POST
        Then status 204

        Given path 'datacollection/form'
        And request someDataCollectionFormArray[2]
        When method POST
        Then status 204

        Given path 'datacollection/form'
        And request someDataCollectionFormArray[3]
        When method POST
        Then status 204
#       <--->

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","payerFormName":"Payer#1","parentFormName":"Parent#1","surveyForms":["Survey#1", 'Survey#2']}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2
        And match response[*].name !contains 'someRule#1'

        Given path ishPath
        And request rule
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 3
        And match response[?(@.name=='someRule#1')].enrolmentFormName contains "Waiting list form (Enrolment)"
        And match response[?(@.name=='someRule#1')].applicationFormName contains "Default Field form (Application)"
        And match response[?(@.name=='someRule#1')].waitingListFormName contains "Accredited course enrolment form (Waiting List)"
        And match response[?(@.name=='someRule#1')].payerFormName contains "Payer#1"
        And match response[?(@.name=='someRule#1')].parentFormName contains "Parent#1"
        And match response[?(@.name=='someRule#1')].surveyForms[*] contains ['Survey#1', 'Survey#2']

#       Scenario have been finished. Now find and remove created objects from DB
#       <--->
        * call read('../../removeEntity.feature') {path: '#(ishPath)', entityName: 'someRule#1'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2
        And match response[*].name !contains 'someRule#1'

        Given path 'datacollection/form'
        When method GET
        Then status 200

        * def id001 = get[0] response[?(@.name == 'Survey#1')].id
        * def id002 = get[0] response[?(@.name == 'Survey#2')].id
        * def id003 = get[0] response[?(@.name == 'Payer#1')].id
        * def id004 = get[0] response[?(@.name == 'Parent#1')].id

        * call read('../../removeEntityById.feature') {path: '#(datacollectionForm)', entityId: '#(id001)'}
        * call read('../../removeEntityById.feature') {path: '#(datacollectionForm)', entityId: '#(id002)'}
        * call read('../../removeEntityById.feature') {path: '#(datacollectionForm)', entityId: '#(id003)'}
        * call read('../../removeEntityById.feature') {path: '#(datacollectionForm)', entityId: '#(id004)'}
#       <--->


    Scenario: (-) Create new invalid (empty) DataCollectionRule

        * def rule = {}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Rule name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid ('Name' field is empty) DataCollectionRule

        * def rule = {"name":"","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","payerFormName":"Payer#1","parentFormName":"Parent#1","surveyForms":["Survey#1", "Survey#2"]}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Rule name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2


    Scenario: (-) Create new invalid ('Name' field is null) DataCollectionRule

        * def rule = {"name":null,"enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","payerFormName":"Payer#1","parentFormName":"Parent#1","surveyForms":["Survey#1", "Survey#2"]}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Rule name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create DataCollectionRule with not unique name

#       Prepare new DataCollectionRule
#       <--->
        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

        Given path ishPath
        And request rule
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 3
        And match response[*].name contains 'someRule#1'
#       <--->

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Rule name should be unique"

#       Scenario have been finished. Now find and remove created objects from DB
#       <--->
        * call read('../../removeEntity.feature') {path: '#(ishPath)', entityName: 'someRule#1'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2
        And match response[*].name !contains 'someRule#1'
#       <--->



    Scenario: (-) Create new invalid ('Enrolment' field is empty) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Enrolment form name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2


    Scenario: (-) Create new invalid ('Enrolment' field is null) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":null,"applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Enrolment form name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid (not existing'Enrolment' value) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"notExisting","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Enrolment form notExisting is not exist"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid ('Application' field is empty) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

         Given path ishPath
         And request rule
         When method POST
         Then status 400
         And match response.errorMessage == "Application form name can not be empty"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 2


    Scenario: (-) Create new invalid ('Application' field is null) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":null,"waitingListFormName":"Accredited course enrolment form (Waiting List)"}

         Given path ishPath
         And request rule
         When method POST
         Then status 400
         And match response.errorMessage == "Application form name can not be empty"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid (not existing 'Application' value) DataCollectionRule

         * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"notExisting","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

         Given path ishPath
         And request rule
         When method POST
         Then status 400
         And match response.errorMessage == "Application form notExisting is not exist"

         Given path ishPath
         When method GET
         Then status 200
         And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid ('Waiting List' field is empty) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":""}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Waiting list form name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2


    Scenario: (-) Create new invalid ('Waiting List' field is null) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":null}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Waiting list form name can not be empty"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid (not existing 'Waiting List' value) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"notExisting"}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Waiting list form notExisting is not exist"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid (not existing 'Survey' value) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","surveyForms":["notExisting#1", "notExisting#2"]}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Survey form notExisting#1 is not exist"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid (not existing 'Payer' value) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","payerFormName":"notExisting"}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Payer form notExisting is not exist"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Create new invalid (not existing 'Parent' value) DataCollectionRule

        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","parentFormName":"notExisting"}

        Given path ishPath
        And request rule
        When method POST
        Then status 400
        And match response.errorMessage == "Parent form notExisting is not exist"

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2



    Scenario: (-) Update existing DataCollectionRule

#       Prepare new DataCollectionForm
#       <--->
        * def rule = {"name":"someRule#1","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

        Given path ishPath
        And request rule
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 3
        And match response[*].name contains 'someRule#1'
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'someRule#1')].id
        * def someDataCollectionFormToUpdate = {id: '#(id)',"name":"someRule#2","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)"}

        Given path ishPath
        And request someDataCollectionFormToUpdate
        When method POST
        Then status 204

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 4
        * def id1 = get[0] response[?(@.name == 'someRule#1')].id
        * def id2 = get[0] response[?(@.name == 'someRule#2')].id

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 2
        And match response[*].name !contains ['someRule#1', 'someRule#2']





