@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/report'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/report'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


        
    Scenario: (+) Delete custom Report by admin

#       <----->  Add a new entity for deleting and get id:
        * def newReport =
        """
        {
        "name":"delete Report01",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"delete1",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['delete Report01'])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete custom Report by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newReport =
        """
        {
        "name":"delete Report02",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"delete2",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['delete Report02'])].id
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
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete custom Report by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newReport =
        """
        {
        "name":"delete Report03",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"delete3",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['delete Report03'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete report. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete system Report

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['Site List'])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match response.errorMessage == "Template provided by ish cannot be deleted."



    Scenario: (-) Delete NOT existing Report

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

