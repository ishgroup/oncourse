@parallel=false
Feature: Main feature for all GET requests with path 'filter'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'filter'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get custom filters list for Qualifications

        Given path ishPath
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $ == []



    Scenario: (+) Get custom filters list for Qualifications by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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
#       <--->

        Given path ishPath
        And param entity = 'Qualification'
        When method GET
        Then status 200
        And match $ == []



    Scenario: (+) Get custom filters list for Qualifications by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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
        And param entity = 'Account'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view custom filters for this entity. Please contact your administrator"
