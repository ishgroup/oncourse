@parallel=false
Feature: Main feature for all GET requests with path 'preference/field/type'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/field/type'
        

    Scenario: (+) Get all fieldTypes
        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 4
