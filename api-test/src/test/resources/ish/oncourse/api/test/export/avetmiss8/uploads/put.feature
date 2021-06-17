@parallel=false
Feature: Main feature for all PUT requests with path 'export/avetmiss8/uploads'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'export/avetmiss8/uploads'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Change funding upload status by admin

#       <---> Change status to 'fail':
        Given path ishPath
        And request {"id":104,"status":"fail"}
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["fail"]

#       <---> Change status to 'unknown':
        Given path ishPath
        And request {"id":104,"status":"unknown"}
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["unknown"]

#       <---> Change status to 'success':
        Given path ishPath
        And request {"id":104,"status":"success"}
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["success"]



    Scenario: (+) Change funding upload status by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

#       <---> Change status to 'fail':
        Given path ishPath
        And request {"id":104,"status":"fail"}
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["fail"]

#       <---> Change status to 'unknown':
        Given path ishPath
        And request {"id":104,"status":"unknown"}
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["unknown"]

#       <---> Change status to 'success':
        Given path ishPath
        And request {"id":104,"status":"success"}
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["success"]



    Scenario: (-) Change funding upload status by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

#       <---> Change status to 'fail':
        Given path ishPath
        And request {"id":104,"status":"fail"}
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions for avetmiss. Please contact your administrator"



    Scenario: (-) Change funding upload status for non-existing entity

        Given path ishPath
        And request {"id":99999,"status":"fail"}
        When method PUT
        Then status 400
        And match $.errorMessage == "FundingUpload with id:99999 doesn't exist"



    Scenario: (-) Change funding upload status to incorrect value

        Given path ishPath
        And request {"id":104,"status":"incorrect"}
        When method PUT
        Then status 400
        And match $.errorMessage == "Incorrect status for funding upload."

#       <---> Verifying that status is not changed:
        Given path ishPath
        When method GET
        Then status 200

        * def status = get response[?(@.id == 104)].status
        * print "status = " + status
        * match status == ["success"]
