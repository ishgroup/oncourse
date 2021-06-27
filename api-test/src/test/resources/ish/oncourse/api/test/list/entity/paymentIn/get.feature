@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/paymentIn'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/paymentIn'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Payment In by admin

        Given path ishPathList
        And param entity = 'PaymentIn'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1", "2", "101"]



    Scenario: (+) Get list of all Payment In by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'PaymentIn'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1", "2", "101"]



    Scenario: (+) Get Payment In by admin

        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":101,
        "payerId":4,
        "payerName":"stud3",
        "paymentInType":"Cash",
        "status":"Success",
        "amount":1800.00,
        "accountInName":"Deposited funds 11100",
        "source":"office",
        "ccTransaction":"",
        "emailConfirmation":false,
        "datePayed":"2018-11-30",
        "dateBanked":null,
        "ccSummary":[],
        "chequeSummary":{},
        "createdBy":"admin@gmail.com",
        "invoices":[{"id":101,"dateDue":"2018-11-30","invoiceNumber":3,"amountOwing":0.00,"amount":1800.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (+) Get Payment In by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,"payerId":3,
        "payerName":"stud2",
        "paymentInType":"Cash",
        "status":"Success",
        "amount":1800.00,
        "accountInName":"Deposited funds 11100",
        "source":"office",
        "ccTransaction":"",
        "emailConfirmation":false,
        "datePayed":"2018-11-29",
        "dateBanked":null,
        "ccSummary":[],
        "chequeSummary":{},
        "createdBy":"admin@gmail.com",
        "invoices":[{"id":2,"dateDue":"2018-11-29","invoiceNumber":2,"amountOwing":0.00,"amount":1800.00}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "administrationCenterId":200,
        "administrationCenterName":"Default site"
        }
        """



    Scenario: (-) Get list of all Payment In by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Paymentin'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get Payment In by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + "/1"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get payment in. Please contact your administrator"



    Scenario: (-) Get not existing Payment In

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
