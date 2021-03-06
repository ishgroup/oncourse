    @parallel=false
Feature: Main feature for all GET requests with path 'preference/concession/type'
    
    
    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/concession/type'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Get all concession types
        Given path ishPath
        When method GET
        Then status 200
        And assert response.length > 0