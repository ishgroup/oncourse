@parallel=false
Feature: Main feature for all GET requests with path 'user/preference'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/preference'
        * def ishPathLogin = 'login'



    Scenario: (+) Get dashboard columns preferences by admin

#       <---> Create default record in db to GET it:
        * def setCategoryWidth = {"key":"html.dashboard.category.width","value":"425"}

        Given path ishPath
        And request setCategoryWidth
        When method PUT
        Then status 204
#       <--->

        Given path ishPath
        And param keys = "html.dashboard.category.width"
        When method GET
        Then status 200
        And match $ == {"html.dashboard.category.width":"425"}



    Scenario: (+) Get dashboard columns preferences by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

#       <---> Create default record in db to GET it:
        * def setCategoryWidth = {"key":"html.dashboard.category.width","value":"425"}

        Given path ishPath
        And request setCategoryWidth
        When method PUT
        Then status 204
#       <--->

        Given path ishPath
        And param keys = "html.dashboard.category.width"
        When method GET
        Then status 200
        And match $ == {"html.dashboard.category.width":"425"}



    Scenario: (+) Get current global theme by admin

#       <---> Create default global theme in db to GET it:
        Given path ishPath
        And request {"key":"html.global.theme","value":"default"}
        When method PUT
        Then status 204
#       <--->

        Given path ishPath
        And param keys = "html.global.theme"
        When method GET
        Then status 200
        And match $ == {"html.global.theme":"default"}



    Scenario: (+) Get current global theme by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

#       <---> Create default global theme in db to GET it:
        Given path ishPath
        And request {"key":"html.global.theme","value":"default"}
        When method PUT
        Then status 204
#       <--->

        Given path ishPath
        And param keys = "html.global.theme"
        When method GET
        Then status 200
        And match $ == {"html.global.theme":"default"}