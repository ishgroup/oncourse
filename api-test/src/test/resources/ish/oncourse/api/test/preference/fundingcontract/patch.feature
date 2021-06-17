@parallel=false
Feature: Main feature for all PATCH requests with path 'preference/fundingcontract'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/fundingcontract'
        


    Scenario: (+) Update Funding Contract status by admin

#       <--->  Add new entity and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-700"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-700')].id
        * def activeStatus = get[0] response[?(@.name == 'FC-700')].active

        * print "id = " + id
#       <--->

        Given path ishPath
        And request [{"id":"#(id)","active":true,"flavour":"WA RAPT","name":"FC-700"}]
        When method PATCH
        Then status 204

#       <---> Assertion:
        Given path ishPath
        When method GET
        Then status 200

        * def activeStatus = get[0] response[?(@.name == 'FC-700')].active
        * match activeStatus == true

        * print "active = " + activeStatus

#       <---> Scenario have been finished. Now remove created entity from db:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Funding Contract status by notadmin

#       <--->  Add new entity and get its id:
        * def newFundingContract = [{"active":false,"flavour":"WA RAPT","name":"FC-701"}]

        Given path ishPath
        And request newFundingContract
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'FC-701')].id
        * def activeStatus = get[0] response[?(@.name == 'FC-701')].active

        * print "id = " + id

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

        Given path ishPath
        And request [{"id":"#(id)","active":true,"flavour":"WA RAPT","name":"FC-701"}]
        When method PATCH
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



#    Scenario: (-) Update not existing Funding Contract status
#
#        Given path ishPath
#        And request [{"id":99999,"active":true,"flavour":"WA RAPT","name":"FC-701"}]
#        When method PATCH
#        Then status 400
#        And match $.errorMessage == "FundingSource with id:99999 doesn't exist"