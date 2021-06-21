@parallel=false
Feature: Main feature for all DELETE requests with path 'user'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'user'
        


    Scenario: (-) Delete existing User using path

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.login == 'admin')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 404


