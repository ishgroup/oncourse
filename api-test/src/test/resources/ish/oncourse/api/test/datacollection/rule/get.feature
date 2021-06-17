@parallel=false
Feature: Main feature for all GET requests with path '/datacollection/rule'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/rule'
        


    Scenario: (+) Get all datacollection rules
    
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 3
        
        * match response[*].name contains only ["Non-accredited course", "Accredited course", "Product"]
