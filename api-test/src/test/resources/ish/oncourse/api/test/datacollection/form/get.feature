@parallel=false
Feature: Main feature for all GET requests with path '/datacollection/form'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'datacollection/form'
        

    Scenario: (+) Get all DataCollectionForms
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 19