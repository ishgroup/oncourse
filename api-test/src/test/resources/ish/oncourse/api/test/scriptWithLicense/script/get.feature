@ignore
@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/script'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
  
        
    Scenario: (+) Get list of all scripts by admin

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        And match karate.sizeOf(response.rows) == 46


    Scenario: (+) Get list of all scripts by notadmin with rights

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

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        And match karate.sizeOf(response.rows) == 46


    Scenario: (+) Get list of all scripts by notadmin

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

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        And match karate.sizeOf(response.rows) == 46


    Scenario: (+) Get existing script by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match response.name == 'alert students of related class'


    Scenario: (+) Get existing script by notadmin

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

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match response.name == 'cloudassess course enrolment create'


    Scenario: (-) Get NONexisting script

        Given path ishPath + '/111111'
        When method GET
        Then status 400
        And match response.errorMessage == "Script with id:111111 doesn't exist"
        