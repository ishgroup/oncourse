@parallel=false
Feature: Main feature for all GET requests with path 'preference/contact/relation/type'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/contact/relation/type'
        

    Scenario: (+) Get all ContactRelationTypes
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 5