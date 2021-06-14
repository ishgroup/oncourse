@parallel=false
Feature: Main feature for all GET requests with path 'preference/timezone'

    Background:
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/timezone'

        
    Scenario: (+) Get all timezones by admin

       Given path ishPath
       When method GET
       Then status 200
       And match response contains "Australia/Brisbane"
       And match response contains "Australia/Perth"
       And match response contains "Australia/Sydney"



    Scenario: (+) Get all timezones by notadmin

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
       And match response contains "Australia/Brisbane"
       And match response contains "Australia/Perth"
       And match response contains "Australia/Sydney"
       
       
       