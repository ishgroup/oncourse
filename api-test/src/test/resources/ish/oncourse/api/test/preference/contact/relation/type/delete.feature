@parallel=false
Feature: Main feature for all DELETE requests with path 'preference/contact/relation/type'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/contact/relation/type'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


    Scenario: (+) Delete existing (not system) ContactRelationType

#       Prepare new ContactRelationType to delete it
#       <--->
        * def someContactRelationType = [{"relationName":"someRelationName","reverseRelationName":"someReverseRelationName","portalAccess":true}]

        Given path ishPath
        And request someContactRelationType
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 6
        And match response[*].relationName contains 'someRelationName'

        * def id = get[0] response[?(@.relationName == 'someRelationName')].id
        * call read('../../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5
        And match response[*].description !contains 'someRelationName'


    Scenario: (-) Delete existing (system) ContactRelationType

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.relationName == 'Parent or Guardian')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match response.errorMessage == "System contact relation type cannot be deleted"


    Scenario: (-) Delete not existing ContactRelationType
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Contact relation type is not exist"


    Scenario: (-) Delete ContactRelationType without ID
        Given path ishPath
        When method DELETE
        Then status 405


    Scenario: (-) Delete ContactRelationType with null ID
        Given path ishPath + '/null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Contact relation id 'null' is incorrect. It must contain of only numbers"