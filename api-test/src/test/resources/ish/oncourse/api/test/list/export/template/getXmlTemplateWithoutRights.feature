@ignore
@parallel=false
Feature: Re-usable feature for GET XML-templates without access rights


    Scenario: (+) Get templates by admin

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entityName = entity
        When method GET
        Then status 403
        And match $.errorMessage contains "Sorry, you have no permissions"