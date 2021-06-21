@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/paymentIn/reverse'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/paymentIn/reverse'
        * def ishPathPaymentIn = 'list/entity/paymentIn'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Reverse Payment In by admin

        Given path ishPath + '/10'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathPaymentIn + '/10'
        When method GET
        Then status 200
        And match $.status == "Success (reversed)"



    Scenario: (+) Reverse Payment In by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/11'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 204

#       <---> Assertion under admin:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPathPaymentIn + '/11'
        When method GET
        Then status 200
        And match $.status == "Success (reversed)"



    Scenario: (-) Reverse already reversed Payment In

        Given path ishPath + '/10'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 400
        And match $.errorMessage == "Payment can not be reversed"



    Scenario: (-) Reverse banked Payment In

        Given path ishPath + '/3'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 400
        And match $.errorMessage == "A banked payment can not be reversed. Please unbank this payment first."



    Scenario: (-) Reverse not existing Payment In

        Given path ishPath + '/99999'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
