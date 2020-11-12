@ignore
@parallel=false
Feature: Re-usable feature to create filter by non-admin without access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entity = entity
        And request dataToCreate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create custom filters for this entity. Please contact your administrator"
