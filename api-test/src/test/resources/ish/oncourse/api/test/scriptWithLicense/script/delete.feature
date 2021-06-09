@ignore
@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/script'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'

        
        
    Scenario: (+) Delete existing script

#       <----->  Add a new script for deleting:
        * def scriptToDelete = {"enabled":false,"name":"scriptName100","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n123\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request scriptToDelete
        When method POST
        Then status 204
#       <----->

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName100')]
        * def id = row.id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match response.errorMessage == "Script with id:" + id + " doesn't exist"


    Scenario: (+) Delete existing script by notadmin with access rights

#       <---> Add a new entity to delete and define id:
        * def scriptToDelete = {"enabled":false,"name":"scriptName101","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n123\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request scriptToDelete
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName101')]
        * def id = row.id

#       <--->  Login as notadmin
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


    Scenario: (-) Delete existing script by notadmin without access rights

#       <---> Add a new entity to delete and define id:
        * def scriptToDelete = {"enabled":false,"name":"scriptName102","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n123\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request scriptToDelete
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName102')]
        * def id = row.id

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
        And match $.errorMessage == "Sorry, you have no permissions to delete script. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204


    Scenario: (-) Delete NOT existing script

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Script with id:99999 doesn't exist"
