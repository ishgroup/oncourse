@ignore
@parallel=false
Feature: Re-usable feature for GET CSV-templates


    Scenario: (+) Get templates by admin

        * url 'https://127.0.0.1:8182/a/v1'

        Given path ishPath
        And param entityName = entity
        When method GET
        Then status 200
        And match $[*].name contains ['#(templateName)']