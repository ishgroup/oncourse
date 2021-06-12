@parallel=false
Feature: Main feature for all DELETE requests with path 'preference/field/type'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/field/type'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


    Scenario: (+) Delete existing (not system) fieldType
#       Prepare new fieldType to delete it
#       <--->
        * def someFieldType = [{"dataType":"Text","name":"fieldType#1","defaultValue":"someValue","fieldKey":"fieldKey1","mandatory":false,"sortOrder":0,"entityType":"Enrolment"}]

        Given path ishPath
        And request someFieldType
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'fieldType#1')].id
        * call read('../../../removeEntityById.feature') {path: '#(ishPath)', entityId: '#(id)'}

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].name !contains 'fieldType#1'


    Scenario: (-) Delete not existing fieldType
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Custom field type is not exist"


    Scenario: (-) Delete fieldType with null ID
        Given path ishPath + '/null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Custom field id 'null' is incorrect. It must contain of only numbers"