    @parallel=false
Feature: Main feature for all GET requests with path 'preference/messagequeued'
    
    
    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def messagePath = 'preference/messagequeued'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Get messages count with type 'sms'
        Given path messagePath
        And param type = 'sms'
        When method GET
        Then status 200
        And assert response >= 4
        
        
    Scenario: (+) Get messages count with type 'email'
        Given path messagePath
        And param type = 'email'
        When method GET
        Then status 200
        And assert response >= 4
        
        
    Scenario: (-) Get messages count with type 'post'
        Given path messagePath
        And param type = 'post'
        When method GET
        Then status 400
        And match response.errorMessage == "Incorrect message type: post"
            
            
    Scenario: (-) Get messages count with incorrect type
        Given path messagePath
        And param type = 'sms1'
        When method GET
        Then status 400
        And match response.errorMessage == "Incorrect message type: sms1"
        
        
    Scenario: (-) Get messages count without param
        Given path messagePath
        When method GET
        Then status 400
        And match response.errorMessage == "Incorrect message type: null"