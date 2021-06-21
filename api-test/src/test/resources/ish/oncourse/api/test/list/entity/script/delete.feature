@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/script' without license

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        
      

    Scenario: (-) Delete NOT existing script

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."
