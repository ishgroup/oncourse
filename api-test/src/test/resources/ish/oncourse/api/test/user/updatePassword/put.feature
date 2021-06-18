@parallel=false
Feature: Main feature for all PUT requests with path 'user/updatePassword'

    Background: Authorize first
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/updatePassword'
        * def ishPathLogin = 'login'
        * def ishPathPreference = 'preference'


    Scenario: (+) Change password by admin to new valid (5 chars) value
        * configure headers = { Authorization: 'admin' }

#       Change password to new value
        Given path ishPath + '/' + '123aB'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:

        * configure headers = null

        * def loginBody = {login: 'admin', password: '123aB', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Change password by notadmin to new valid (5 chars) value

#       Change password to new value
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + '123aB'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: '123aB', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->



