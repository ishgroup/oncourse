@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/room/validation'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/room/validation'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (-) Validate assigned room deleting

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete room assigned to sessions."


    Scenario: (-) Validate not assigned room deleting

        Given path ishPath + '/3'
        When method DELETE
        Then status 204