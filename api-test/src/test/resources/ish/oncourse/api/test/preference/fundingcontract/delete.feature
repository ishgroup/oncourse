@parallel=false
Feature: Main feature for all DELETE requests with path 'preference/fundingcontract'
    
    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/fundingcontract'
        



    Scenario: (+) Delete existing Funding Contract

#       <--->  Add new entity to delete:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-10"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-10')].id
        * print "id = " + id

#       <---> Delete entity:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match $[*].name !contains "FC-10"



    Scenario: (+) Delete existing Funding Contract by notadmin

#       <--->  Add new entity to delete:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-10"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-10')].id
        * print "id = " + id

#       <--->  Login as notadmin and delete entity:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403

#       <---> Scenario have been finished. Now remove created entity from db:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
        

    Scenario: (-) Delete Funding Contract assigned to enrolments

        Given path ishPath + '/13'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete funding contract. It's assigned to enrolments"


    Scenario: (-) Delete not existing Funding Contract
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Entity with id = '100000' doesn't exist"
