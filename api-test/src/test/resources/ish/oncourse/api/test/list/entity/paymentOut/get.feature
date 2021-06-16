@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/paymentOut'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/paymentOut'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Payment Out by admin

        Given path ishPathList
        And param entity = 'PaymentOut'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000"]



    Scenario: (+) Get list of all Payment Out by notadmin with access rights

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
        And param entity = 'PaymentOut'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000"]



    Scenario: (+) Get Payment Out by admin

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":"#number",
        "administrationCenterName":"#string"
        }
        """



    Scenario: (+) Get Payment Out by notadmin with access rights

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

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "payeeId":20,
        "payeeName":"student1 PaymentOut",
        "type":"Other",
        "chequeSummary":{},
        "paymentMethodId":1,
        "refundableId":null,
        "status":"Success",
        "accountOut":1,
        "amount":10.00,
        "datePayed":"2019-09-19",
        "dateBanked":null,
        "invoices":[{"id":28,"dateDue":"2019-09-11","invoiceNumber":29,"amountOwing":-390.00,"amount":10.00}],
        "privateNotes":"",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "createdBy":"admin@gmail.com",
        "administrationCenterId":"#number",
        "administrationCenterName":"#string"
        }
        """



    Scenario: (-) Get list of all Payment Out by notadmin without access rights

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
        And param entity = 'Paymentin'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get Payment Out by notadmin without access rights

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

        Given path ishPath + "/1000"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get payment out. Please contact your administrator"



    Scenario: (-) Get not existing Payment Out

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
