@parallel=false
Feature: Main feature for all PUT requests with path 'datacollection/form'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        * def ishPath = 'datacollection/form'


    Scenario: (+) Update existing DataCollectionForm setting 'id' in path and in body

#       Prepare new DataCollectionForm
#       <--->
        * def someDataCollectionForm = {"name":"Survey#7","type":"Survey","fields":[],"headings":[{"name":"Heading#1","description":"Description#1"}],"deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 17
        And match response[*].name contains 'Survey#7'
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'Survey#7')].id
        * def someDataCollectionFormToUpdate = {id: '#(id)',"name":"Survey#7_UPD","type":"Survey","fields":[],"headings":[{"name":"Heading#1_UPD","description":"Description#1_UPD"}],"deliverySchedule":"On start"}

        Given path ishPath + '/' + id
        And request someDataCollectionFormToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 17
        And match response[*].name contains 'Survey#7_UPD'
        And match response[*].name !contains 'Survey#7'

        * def list = get response[?(@.name == 'Survey#7_UPD')]
        * match each list[*].deliverySchedule contains 'On start'
        * match each list[*].headings[*].name contains 'Heading#1_UPD'
        * match each list[*].headings[*].description contains 'Description#1_UPD'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * print "Scenario have been finished. Now find and remove created object from DB"
        Given path ishPath
        When method GET
        Then status 200

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}
#       <--->
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains ['Survey#7', 'Survey#7_UPD']


    Scenario: (+) Update existing DataCollectionForm setting 'id' only in path
#       Prepare new DataCollectionForm to update it
#       <--->
        * def someDataCollectionForm = {"name":"Survey#7","type":"Survey","fields":[],"headings":[{"name":"Heading#1","description":"Description#1"}],"deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 17
        And match response[*].name contains 'Survey#7'
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'Survey#7'
        And match response[*].name !contains 'Survey#7_UPD'
        * def id = get[0] response[?(@.name == 'Survey#7')].id

        * def someDataCollectionFormToUpdate = {"name":"Survey#7_UPD","type":"Survey","fields":[],"headings":[{"name":"Heading#1_UPD","description":"Description#1_UPD"}],"deliverySchedule":"On start"}
        Given path ishPath + '/' + id
        And request someDataCollectionFormToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'Survey#7'
        And match response[*].name contains 'Survey#7_UPD'

        * def list = get response[?(@.name == 'Survey#7_UPD')]
        * match each list[*].deliverySchedule contains 'On start'
        * match each list[*].headings[*].name contains 'Heading#1_UPD'
        * match each list[*].headings[*].description contains 'Description#1_UPD'

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains ['Survey#7', 'Survey#7_UPD']
#       <--->


    Scenario: (-) Update DataCollectionForm setting 'id' only in body
#       Prepare new DataCollectionForm to update it
#       <--->
        * def someDataCollectionForm = {"name":"Survey#7","type":"Survey","fields":[],"headings":[{"name":"Heading#1","description":"Description#1"}],"deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 17
        And match response[*].name contains 'Survey#7'
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'Survey#7'
        And match response[*].name !contains 'Survey#7_UPD'
        * def id = get[0] response[?(@.name == 'Survey#7')].id

        * def someDataCollectionFormToUpdate = {id: "#(id)","name":"Survey#7_UPD","type":"Survey","fields":[],"headings":[{"name":"Heading#1_UPD","description":"Description#1_UPD"}],"deliverySchedule":"On start"}
        Given path ishPath + '/'
        And request someDataCollectionFormToUpdate
        When method PUT
        Then status 405

#       Scenario have been finished. Now find and remove created object from DB
#       <--->
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains ['Survey#7', 'Survey#7_UPD']
#       <--->


    Scenario: (+) Update DataCollectionForm setting different 'ids' in body and in path
#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm1 = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}
        * def someDataCollectionForm2 = {"name":"Survey#2","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm1
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionForm2
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'Survey#1')].id
        * def id2 = get[0] response[?(@.name == 'Survey#2')].id

        * def dataCollectionFormToUpdate = {id: '#(id1)', "name":"Survey_UPD","type":"Survey","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id2
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name contains 'Survey_UPD'
        * def updatedDataCollectionId = get[0] response[?(@.name == 'Survey_UPD')].id
        * match id1 != updatedDataCollectionId
        * match id2 == updatedDataCollectionId

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains ['Survey#1', 'Survey_UPD']
#       <--->


    Scenario: (-) Update 'Type' to empty

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form type should be specified"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Type' to null

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":null,"deliverySchedule":"On demand"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form type should be specified"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Type' to other existing

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"Application","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form type can not be changed"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Type' to other not existing

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"notExisting","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form type should be specified"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Name' to empty

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"","type":"Survey","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form name can not be empty"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Name' to null

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":null,"type":"Survey","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form name can not be empty"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Name' to other existing

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm1 = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}
        * def someDataCollectionForm2 = {"name":"Survey#2","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm1
        When method POST
        Then status 204

        Given path ishPath
        And request someDataCollectionForm2
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id1 = get[0] response[?(@.name == 'Survey#1')].id
        * def id2 = get[0] response[?(@.name == 'Survey#2')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}
        Given path ishPath + '/' + id2
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Form name should be unique"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id1)'}
        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id2)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains ['Survey#1', 'Survey#2']
#       <--->


    Scenario: (-) Update 'Delivery Schedule' to empty

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"Survey","deliverySchedule":""}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Delivery schedule required for Survey form"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Delivery Schedule' to null

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"Survey","deliverySchedule":null}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Delivery schedule required for Survey form"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update 'Delivery Schedule' to not existing

#       Prepare new DataCollectionForms to update
#       <--->
        * def someDataCollectionForm = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}

        Given path ishPath
        And request someDataCollectionForm
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        * def id = get[0] response[?(@.name == 'Survey#1')].id

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"Survey","deliverySchedule":"notExisting"}
        Given path ishPath + '/' + id
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "Delivery schedule required for Survey form"

#       Scenario have been finished. Now find and remove created object from DB

        * call read('../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 16
        And match response[*].name !contains 'Survey#1'
#       <--->


    Scenario: (-) Update not existing DataCollectionForm

        * def dataCollectionFormToUpdate = {"name":"Survey#1","type":"Survey","deliverySchedule":"On demand"}
        Given path ishPath + '/99999'
        And request dataCollectionFormToUpdate
        When method PUT
        Then status 400
        And match response.errorMessage == "The data collection form 99999 is not exist"

