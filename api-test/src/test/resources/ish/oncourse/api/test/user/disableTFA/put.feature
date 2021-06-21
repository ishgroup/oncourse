@parallel=false
Feature: Main feature for all PUT requests with path 'user/disableTFA'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/disableTFA'
        * def ishPathUser = 'user'
        * def ishPathLogin = 'login'



    Scenario: (+) Disable TFA for admin

#       <----->  Check TFA for user
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "2fa_admin1", password: "password", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Token required"
        And match response.errorMessage == "Auth Token required"
#       <----->

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "admin", password: "password", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathUser
        When method GET
        Then status 200

        * def id = get[0] response[?(@.login == '2fa_admin1')].id

        Given path ishPath + '/' + id
        And request {}
        When method PUT
        Then status 204

#       <----->  Check Log in without TFA
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "2fa_admin1", password: "password", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (-) Disable TFA for non-existing user

        Given path ishPath + '/99999'
        And request {}
        When method PUT
        Then status 400
        And match $.errorMessage == "SystemUser with id:99999 doesn't exist"



