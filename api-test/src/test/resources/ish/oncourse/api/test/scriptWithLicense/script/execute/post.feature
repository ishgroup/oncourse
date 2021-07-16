@ignore
@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/script/execute'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script/execute'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'
        


    Scenario: (+) Execute script by admin

#       POST request requires body
        * def requiredBody = {"scriptId":"39","entity":null,"entityIds":[]}

        Given path ishPath
        And request requiredBody
        When method POST
        Then status 200

        * match $ == {"status":"Finished","message":null}


    Scenario: (-) Execute script by notadmin
        * configure headers = { Authorization: 'UserWithRightsView' }
        * def requiredBody = {"scriptId":"39","entity":null,"entityIds":[]}

        Given path ishPath
        And request requiredBody
        When method POST
        Then status 200

        * match $ == {"status":"Finished","message":null}