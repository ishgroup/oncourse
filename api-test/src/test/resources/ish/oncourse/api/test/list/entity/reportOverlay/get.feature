@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/reportOverlay'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/reportOverlay'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get overlay by admin

        Given path ishPath + '/100'
        When method GET
        Then status 200
        And match $ == {"id":100,"name":"Certificate","preview":"#ignore"}



    Scenario: (+) Get overlay by notadmin

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

        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $ == {"id":101,"name":"OROR Training","preview":"#ignore"}


