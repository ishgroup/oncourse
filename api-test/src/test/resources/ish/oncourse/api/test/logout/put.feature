@parallel=false
Feature: Main feature for all PUT requests with path 'logout'

    Background: Authorize first
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'logout'
        * def ishPathContact = 'list/entity/contact'
        



    Scenario: (+) Logout as admin

#       <---> Login:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'
#       <--->

        Given path ishPath
        And request {}
        When method PUT
        Then status 204

#       <---> Verification of logout:
        Given path ishPathContact + '/2'
        When method GET
        Then status 401
        And match $ contains "Error 401 Unauthorized"



    Scenario: (+) Logout as notadmin with rights Delete

#       <---> Login:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'
#       <--->

        Given path ishPath
        And request {}
        When method PUT
        Then status 204

#       <---> Verification of logout:
        Given path ishPathContact + '/2'
        When method GET
        Then status 401
        And match $ contains "Error 401 Unauthorized"



    Scenario: (+) Logout as notadmin with rights Hide

#       <---> Login:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'
#       <--->

        Given path ishPath
        And request {}
        When method PUT
        Then status 204

#       <---> Verification of logout:
        Given path ishPathContact + '/2'
        When method GET
        Then status 401
        And match $ contains "Error 401 Unauthorized"



    Scenario: (-) Logout from not existing session

        Given path ishPath
        And request {}
        When method PUT
        Then status 401
