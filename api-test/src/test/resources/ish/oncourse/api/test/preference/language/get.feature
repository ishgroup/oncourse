@parallel=false
Feature: Main feature for all GET requests with path 'preference/language'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/language'
        

        

    Scenario: (+) Get languages by admin

        Given path ishPath
        When method GET
        Then status 200
        And match response ==
        """
        [
        {"id":206,"absCode":null,"isActive":true,"name":"England"},
        {"id":358,"absCode":null,"isActive":true,"name":"Belorussian"},
        {"id":756,"absCode":null,"isActive":true,"name":"Russian"},
        {"id":1178,"absCode":null,"isActive":true,"name":"Aboriginal"}
        ]
        """



    Scenario: (+) Get languages by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response ==
        """
        [
        {"id":206,"absCode":null,"isActive":true,"name":"England"},
        {"id":358,"absCode":null,"isActive":true,"name":"Belorussian"},
        {"id":756,"absCode":null,"isActive":true,"name":"Russian"},
        {"id":1178,"absCode":null,"isActive":true,"name":"Aboriginal"},
        ]
        """