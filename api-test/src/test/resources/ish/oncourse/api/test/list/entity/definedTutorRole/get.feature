@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/definedTutorRole'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/definedTutorRole'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all Defined Tutor Roles by admin

        Given path ishPathList
        And param entity = 'DefinedTutorRole'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7"]



    Scenario: (+) Get list of all Defined Tutor Roles by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'DefinedTutorRole'
        When method GET
        Then status 200


    Scenario: (+) Get Defined Tutor Role by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ == {"id":1,"name":"Tutor","description":"Tutor","active":true,"payRates":[]}



    Scenario: (+) Get Defined Tutor Role by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ == {"id":2,"name":"Lecturer","description":"Lecturer","active":true,"payRates":[]}



    Scenario: (-) Get not existing Defined Tutor Role

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

