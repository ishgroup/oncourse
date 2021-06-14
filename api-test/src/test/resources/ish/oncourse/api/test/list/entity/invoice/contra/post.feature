@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/invoice/contra'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/invoice/contra'
        * def ishPathLogin = 'login'
        * def ishPathInvoice = 'list/entity/invoice'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create contra invoice by admin

        Given path ishPath + '/8'
        And request ["7"]
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathInvoice + '/8'
        When method GET
        Then status 200
        And match $.amountOwing == 0.00

        Given path ishPathInvoice + '/7'
        When method GET
        Then status 200
        And match $.amountOwing == 40.00



    Scenario: (+) Create contra invoice for several invoices

        Given path ishPath + '/15'
        And request ["12","13","14"]
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathInvoice + '/15'
        When method GET
        Then status 200
        And match $.amountOwing == 0.00

        Given path ishPathInvoice + '/12'
        When method GET
        Then status 200
        And match $.amountOwing == 0.00

        Given path ishPathInvoice + '/13'
        When method GET
        Then status 200
        And match $.amountOwing == 0.00

        Given path ishPathInvoice + '/14'
        When method GET
        Then status 200
        And match $.amountOwing == 50.00



    Scenario: (+) Create contra invoice by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/10'
        And request ["9"]
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathInvoice + '/10'
        When method GET
        Then status 200
        And match $.amountOwing == 0.00

        Given path ishPathInvoice + '/9'
        When method GET
        Then status 200
        And match $.amountOwing == 60.00



    Scenario: (-) Create contra invoice by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + "/8"
        And request ["7"]
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update credit note. Please contact your administrator"



    Scenario: (-) Create contra invoice when invoice is not a credit note

        Given path ishPath + "/8"
        And request ["7"]
        When method POST
        Then status 400
        And match $.errorMessage == "Invoice with id=8 is not a credit note."



    Scenario: (-) Create contra invoice for not existing invoice

        Given path ishPath + "/99999"
        And request ["7"]
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."





