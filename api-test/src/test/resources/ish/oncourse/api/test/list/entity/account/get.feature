@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/account'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/account'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all accounts by admin

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8","9","10","11","12"]



    Scenario: (+) Get list of all accounts by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8","9","10","11","12"]



    Scenario: (+) Get account by admin

        Given path ishPath + "/1"
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1,
        "accountCode":"11100",
        "description":"Deposited funds",
        "isEnabled":true,
        "type":"asset",
        "tax":null
        }
        """



    Scenario: (+) Get account by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + "/7"
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":7,
        "accountCode":"41000",
        "description":"Student enrolments",
        "isEnabled":true,
        "type":"income",
        "tax":
            {
            "id":1,
            "code":"GST",
            "editable":null,
            "systemType":null,
            "gst":null,
            "rate":null,
            "payableAccountId":null,
            "receivableAccountId":null,
            "description":null,
            "created":null,
            "modified":null
            }
        }
        """


    Scenario: (-) Get list of all accounts by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Account'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get account by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/7"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get account. Please contact your administrator"



    Scenario: (-) Get not existing account

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Account with id:9999 doesn't exist"

