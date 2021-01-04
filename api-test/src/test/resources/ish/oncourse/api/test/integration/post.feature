@parallel=false
Feature: Main feature for all POST requests with path 'integration'
    
    
    Background: Authorize first
        * callonce read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'integration'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Create MYOB integration
        * def integration = {name: 'someName', type: 6}
        Given path ishPath
        And request integration
        When method POST
        Then status 204
        
        Given path ishPath
        When method GET
        Then status 200
        And match response[0].type == 6
        And match response[0].name == 'someName'
        And match response[0].verificationCode == null
        And match each response[0].props[*].value == null

#       Scenario have been finished. Now find and remove created object from DB
        * call read('../removeEntity.feature') {path: '#(ishPath)', entityName: 'someName'}



