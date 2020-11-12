@ignore
@parallel=false
Feature: Re-usable feature to get list for each entity without access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entity = entity
        When method GET
        Then status 403
        And match $.errorMessage contains "Sorry, you have no permissions"

