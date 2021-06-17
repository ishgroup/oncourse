@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/script/execute' without license

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script/execute'
        * def ishPathLogin = 'login'
        


    Scenario: (+) Execute script by admin

#       POST request requires body
        * def requiredBody = {"scriptId":51,"variables":{},"searchQuery":{"search":"","filter":"","pageSize":50,"offset":0,"tagGroups":[]}}

        Given path ishPath
        And request requiredBody
        When method POST
        Then status 200

        * match $ == "#string"


    Scenario: (-) Execute script by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"


        * def requiredBody = {"scriptId":51,"variables":{},"searchQuery":{"search":"","filter":"","pageSize":50,"offset":0,"tagGroups":[]}}

        Given path ishPath
        And request requiredBody
        When method POST
        Then status 200

        * match $ == "#string"