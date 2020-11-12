@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/audit'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/audit'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all audit records by admin

        Given path ishPathList
        And param entity = 'Audit'
        And param search = "id in ( 1, 2, 3 )"
        When method GET
        Then status 200
        And match $.rows[*].id contains ["3","2","1"]



    Scenario: (+) Get audit record by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1,
        "message":"Entity updated: database.used",
        "action":"UPDATE",
        "systemUser":"onCourse Administrator",
        "entityId":"1",
        "entityIdentifier":"SystemUser",
        "created":"2019-06-03T09:33:54.412Z"
        }
        """



    Scenario: (+) Get list of all audit records by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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
        And param entity = 'Audit'
        And param search = "id in ( 1, 2, 3 )"
        When method GET
        Then status 200
        And match $.rows[*].id contains ["3","2","1"]



    Scenario: (+) Get audit record by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "message":"Entity created: account.default.debtors.id",
        "action":"CREATE",
        "systemUser":"onCourse Administrator",
        "entityId":"1004",
        "entityIdentifier":"Preference",
        "created":"#ignore"
        }
        """



    Scenario: (-) Get list of all audit records by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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
        And param entity = 'Audit'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get audit record by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        Given path ishPath + '/1'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get audits. Please contact your administrator"



#    Scenario: (-) Get not existing audit record
#
#        Given path ishPath + "/999999"
#        When method GET
#        Then status 400
#        And match $.errorMessage == "Record with id:999999 doesn't exist"
#
#
#
#    Scenario: (-) Get existing audit record without id in path
#
#        Given path ishPath
#        When method GET
#        Then status 405