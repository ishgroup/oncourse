@parallel=false
Feature: Main feature for all PUT requests with path '/datacollection/rule'


    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/rule'
        * def datacollectionForm = 'datacollection/form'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


    Scenario: (+) Update existing DataCollectionRule setting 'id' in path and in body

#       Prepare new 'Survey', 'Payer', 'Parent' DataCollectionForms and creating a new DataCollectionRule
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

        * def rule = {"name":"someRule#1","productFormName":"Default Field form (Product)", "voucherFormName":"Default Field form (Voucher)","membershipFormName":"Default Field form (Membership)","enrolmentFormName":"Waiting list form (Enrolment)","applicationFormName":"Default Field form (Application)","waitingListFormName":"Accredited course enrolment form (Waiting List)","payerFormName":"Payer#1","parentFormName":"Parent#1","surveyForms":["Survey#1", 'Survey#2']}

        Given path ishPath
        And request rule
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'someRule#1'
#       <--->

        * def id = get[0] response[?(@.name == 'someRule#1')].id
        * def ruleToUpdate = {id:"#(id)","name":"someRule#UPD","productFormName":"Default Field form (Product)","voucherFormName":"Default Field form (Voucher)","membershipFormName":"Default Field form (Membership)","enrolmentFormName":"Default Field form (Enrolment)","applicationFormName":"Waiting list form (Application)","waitingListFormName":"Application form (Waiting List)","payerFormName":"","parentFormName":"","surveyForms":[]}

        Given path ishPath + '/' + id
        And request ruleToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someRule#1'
        And match response[?(@.name=='someRule#UPD')].enrolmentFormName contains "Default Field form (Enrolment)"
        And match response[?(@.name=='someRule#UPD')].applicationFormName contains "Waiting list form (Application)"
        And match response[?(@.name=='someRule#UPD')].waitingListFormName contains "Application form (Waiting List)"
        And match each response[?(@.name=='someRule#UPD')].payerFormName == null
        And match each response[?(@.name=='someRule#UPD')].parentFormName == null
        And match response[?(@.name=='someRule#UPD')].surveyForms[*] contains []

#       Scenario have been finished. Now find and remove created objects from DB
#       <--->
        * call read('../../removeEntity.feature') {path: '#(ishPath)', entityName: 'someRule#UPD'}

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'someRule#UPD'

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

