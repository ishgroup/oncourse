@ignore
@parallel=false
Feature: re-usable feature to edit 'readOnly' preference property and check status 400 returns
    Background:
        * url 'https://127.0.0.1:8182/a/v1'

    Scenario:
        Given path 'preference'
        And request [{ uniqueKey: '#(code)', valueString: '#(value)' }]
        When method POST
        Then status 400
        And match response.errorMessage == "preference read only"