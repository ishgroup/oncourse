@parallel=false
Feature: Main feature for all GET requests with path 'user'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'user'
        


    Scenario: (+) Get all users by admin

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains ['admin', 'notadmin', '2fa', 'inactive']


    Scenario: (-) Get all users by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 403







