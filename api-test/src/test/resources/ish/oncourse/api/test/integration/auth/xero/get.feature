@parallel=false
Feature: Main feature for all GET requests with path 'integration/auth/xero'


    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'integration/auth/xero'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


    Scenario: (+) Get all 'xero' integrations
        Given path ishPath
        When method GET
        Then status 200
