@parallel=false
Feature: Main feature for all GET requests with path 'preference/language'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
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
        * configure headers = { Authorization:  'UserWithRightsHide'}

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
