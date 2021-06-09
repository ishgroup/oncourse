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

