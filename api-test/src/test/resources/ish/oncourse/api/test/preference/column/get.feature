@parallel=false
Feature: Main feature for all GET requests with path 'preference/column'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'preference/column'
        
        

    Scenario: (+) Get column view settings

        Given path ishPath
        When method GET
        Then status 200
        And match response == {"preferenceLeftColumnWidth":200,"tagLeftColumnWidth":200,"securityLeftColumnWidth":200,"automationLeftColumnWidth":250}