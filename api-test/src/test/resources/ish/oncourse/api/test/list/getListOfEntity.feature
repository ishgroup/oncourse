@ignore
@parallel=false
Feature: Re-usable feature to get list for each entity using GET request


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entity = entity
        When method GET
        Then status 200
        And match $.entity == entity
