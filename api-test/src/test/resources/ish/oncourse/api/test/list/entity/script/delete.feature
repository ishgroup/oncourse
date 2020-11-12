@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/script' without license

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
      

    Scenario: (-) Delete NOT existing script

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."


    Scenario: (-) Delete script without any ID

            Given path ishPath + '/'
            When method DELETE
            Then status 405


    Scenario: (-) Delete script with NULL as ID

            Given path ishPath + '/null'
            When method DELETE
            Then status 404


    Scenario: (-) Delete script without path

           Given path ishPath
           When method DELETE
           Then status 405

