@ignore
@parallel=false
Feature: re-usable feature to check access

    Background: Configure url, ssl and httpClientClass
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'access'

    Scenario:

        * def parameters = {name: '#(name)'}

        Given path 'list/export/template'
        And param entityName = parameters.name
        When method GET
        Then status 200
        And match $ == '##[_ > 0]'