@ignore
@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/document'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/document'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass document by admin

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $ == []



    Scenario: (+) Get CourseClass document by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ == []



    Scenario: (-) Get not existing CourseClass document

        Given path ishPath + '/99999'
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
