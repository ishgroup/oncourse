@ignore
@parallel=false
Feature: re-usable feature to get password preference property and check it returns 'null' value

    Background: Configure url, ssl and httpClientClass
        * url 'https://127.0.0.1:8182/a/v1'

    Scenario:
        * def preference = {code: '#(code)'}

        Given path 'preference'
        And param search = preference.code
        When method GET
        Then status 200
        And match response[0].uniqueKey == preference.code
        And match response[0].valueString == null
