@ignore
@parallel=false
Feature: Re-usable feature to get PDF without access rights


    Scenario:

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entityName = entity
        And request dataToExport
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to do prints. Please contact your administrator"


