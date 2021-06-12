@parallel=false
Feature: Main feature for all DELETE requests with path 'integration'
    

    Background: Authorize first
        * callonce read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'integration'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Delete existing integration
    
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 0
        
#       Prepare new concession type to delete it
#       <--->
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 1
        And def id = get[0] response[?(@.name == 'someName')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 0
        
        
    Scenario: (-) Delete not existing integration type 
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Integration '100000' is not exist"
        
    
    
    Scenario: (-) Delete integration with null ID
        Given path ishPath + '/null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Integration id 'null' is incorrect. It must contain of only numbers"