@ignore
@parallel=false
Feature: Re-usable feature to get search result


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param search = searchQuery
        When method GET
        Then status 200
        And match $ contains dataToAssert