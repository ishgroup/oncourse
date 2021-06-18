@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/audit'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/audit'
        * def ishPathList = 'list'
        



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
        "message":"Entity created: PaymentIn",
        "action":"CREATE",
        "systemUser":"onCourse Administrator",
        "entityId":"1",
        "entityIdentifier":"PaymentIn",
        "created":"#ignore"
        }
        """



    Scenario: (+) Get list of all audit records by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
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
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "message":"Entity created: Transaction Detail",
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
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
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
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/1'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get audits. Please contact your administrator"


