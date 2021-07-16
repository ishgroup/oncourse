@ignore
@parallel=false
Feature: re-usable feature to check access

    Background: Configure url, ssl and httpClientClass
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'access'

    Scenario:

        * def PermissionRequest = {path: '#(path)', method: '#(method)'}

        Given path ishPath
        And request PermissionRequest
        When method POST
        Then status 200
        And match $ contains result