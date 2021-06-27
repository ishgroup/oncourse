@parallel=false
Feature: Main feature for all GET requests with path 'preference/country'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        


    Scenario: (+) Get all countries
        Given path 'preference/country'
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 3
        And match response[*].name == ["Australia","United States of America","Poland"]