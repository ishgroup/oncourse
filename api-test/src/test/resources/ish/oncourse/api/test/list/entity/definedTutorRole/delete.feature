@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/definedTutorRole'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/definedTutorRole'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing Tutor Role by admin

#       <----->  Add a new entity for deleting and get id:
        * def newTutorRole =
        """
        {
        "name":"tutor role delete01",
        "description":"delete01",
        "active":true,
        "payRates":[{"type":"Per enrolment","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'DefinedTutorRole'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["tutor role delete01"])].id
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



    Scenario: (-) Delete existing Tutor Role by notadmin

#       <----->  Add a new entity for deleting and get id:
        * def newTutorRole =
        """
        {
        "name":"tutor role delete03",
        "description":"delete03",
        "active":true,
        "payRates":[{"type":"Per timetabled hour","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'DefinedTutorRole'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["tutor role delete03"])].id
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
        Then status 403

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



    Scenario: (-) Delete Tutor Role with relation

        Given path ishPath + '/4'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete defined tutor role attached to tutor."



    Scenario: (-) Delete NOT existing Tutor Role

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

