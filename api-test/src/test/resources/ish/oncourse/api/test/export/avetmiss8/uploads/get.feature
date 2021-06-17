@parallel=false
Feature: Main feature for all GET requests with path 'export/avetmiss8/uploads'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8/uploads'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get funding uploads by admin

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].id contains [104,103,102,101,1]



    Scenario: (+) Get funding uploads by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].id contains [104,103,102,101,1]



    Scenario: (-) Get funding uploads by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions for avetmiss. Please contact your administrator"

