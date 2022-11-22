@parallel=false
Feature: Main feature for all PUT requests with path 'login'

    Background: Authorize first
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'login'
        * def ishPathPreference = 'preference'
        * def ishPathPass = 'user/updatePassword'
        * configure headers = null
        
    Scenario: (+) Authorize as admin several times

        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'
        And match response.errorMessage == null



    Scenario: (+) Authorize as notadmin several times

        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'



    Scenario: (+) Authorize as 2fa user

        * def loginBody = {login: '2fa_notadmin1', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'User is disabled. Please contact onCourse Administrator.'



    Scenario: (+) Authorize as admin with Tfa

        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'false'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'TFA optional'



    Scenario: (+) Authorize as notadmin with Tfa

        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'false'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'TFA optional'
