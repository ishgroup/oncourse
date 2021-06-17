@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/sales'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/sales'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Cancel Membership sale by admin

        Given path ishPath
        And request {"createCrediNote":true,"id":1006}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1006'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (+) Cancel Membership sale by notadmin with access rights

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

        Given path ishPath
        And request {"createCrediNote":true,"id":1009,"retainAdministrativeFee":true,"feeAmount":11,"feeTaxId":2,"retainAccountId":8}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1006'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (+) Cancel Article sale by admin

        Given path ishPath
        And request {"createCrediNote":true,"id":1005}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1005'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (+) Cancel Article sale by notadmin with access rights

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

        Given path ishPath
        And request {"createCrediNote":true,"id":1008,"retainAdministrativeFee":true,"feeAmount":11,"feeTaxId":2,"retainAccountId":8}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1008'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (+) Cancel Voucher sale by admin

        Given path ishPath
        And request {"createCrediNote":true,"id":1007}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1007'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (+) Cancel Voucher sale by notadmin with access rights

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

        Given path ishPath
        And request {"createCrediNote":true,"id":1010,"retainAdministrativeFee":true,"feeAmount":11,"feeTaxId":2,"retainAccountId":8}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath + '/1010'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (-) Cancel already cancelled Membership sale by admin

        Given path ishPath
        And request {"createCrediNote":true,"id":1006}
        When method POST
        Then status 400
        And match $.errorMessage == "There is no sale by provided id"



    Scenario: (-) Cancel not existing Membership sale

        Given path ishPath
        And request {"createCrediNote":true,"id":99999}
        When method POST
        Then status 400
        And match $.errorMessage == "There is no sale by provided id"