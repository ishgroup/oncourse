@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/emailTemplate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Get list of all EmailTemplates by admin

        Given path ishPathList
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6"]



    Scenario: (+) Get list of all EmailTemplates by notadmin

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

        Given path ishPathList
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6"]



    Scenario: (+) Get EmailTemplate by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $.id == 1



    Scenario: (+) Get EmailTemplate by notadmin

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
        And match $.id == 2



    Scenario: (-) Get not existing EmailTemplate

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
