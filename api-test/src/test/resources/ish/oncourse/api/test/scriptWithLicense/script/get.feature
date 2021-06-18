@ignore
@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/script'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'



    Scenario: (+) Get list of all scripts by admin

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        And match karate.sizeOf(response.rows) == 46


    Scenario: (+) Get list of all scripts by notadmin with rights
        * configure headers = { Authorization: 'UserWithRightsView'}

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        And match karate.sizeOf(response.rows) == 46


    Scenario: (+) Get list of all scripts by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization: 'UserWithRightsHide'}

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
        * configure headers = { Authorization: 'UserWithRightsHide'}

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match response.name == 'cloudassess course enrolment create'


    Scenario: (-) Get NONexisting script

        Given path ishPath + '/111111'
        When method GET
        Then status 400
        And match response.errorMessage == "Script with id:111111 doesn't exist"
