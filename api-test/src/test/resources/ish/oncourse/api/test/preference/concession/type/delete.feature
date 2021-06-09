@parallel=false
Feature: Main feature for all DELETE requests with path 'preference/concession/type'
    
    
    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/concession/type/'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        
        
    Scenario: (+) Delete existing concession type 
#       Prepare new concession type to delete it
#       <--->
        * def concessionTypeArray = [{name: 'SomeName200', requireExpary: false, requireNumber: false, allowOnWeb: true}]
        Given path ishPath
        And request concessionTypeArray
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And def concessionType = get[0] response[?(@.name == 'SomeName200')]

        Given path ishPath + concessionType.id
        When method DELETE
        Then status 204
        
        
    Scenario: (-) Delete concession type with null ID
        Given path ishPath + 'null'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Concession type id is incorrect. It must consist of only numbers"
        
    Scenario: (-) Delete not existing concession type 
        Given path ishPath + '100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Concession type is not exist"
        