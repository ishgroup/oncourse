@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/courseClass/tutor'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Delete existing Tutor by admin

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
            """
            {
            "classId":6,
            "contactId":5,
            "roleId":1,
            "confirmedOn":"2018-01-01",
            "isInPublicity":true
            }
            """

        Given path ishPath
        And request newTutor
        When method POST
        Then status 200

        Given path ishPath + '/6'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.contactId == 5)].id
        * print "id = " + id
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing Tutor by admin with access rights

#        <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":5,
              "roleId":1,
              "confirmedOn":"2018-01-01",
              "isInPublicity":true
              }
              """

        Given path ishPath
        And request newTutor
        When method POST
        Then status 200

        Given path ishPath + '/6'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.contactId == 5)].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#        <---> Verification of deleting:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete existing Tutor by admin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":5,
              "roleId":1,
              "confirmedOn":"2018-01-01",
              "isInPublicity":true
              }
              """

        Given path ishPath
        And request newTutor
        When method POST
        Then status 200

        Given path ishPath + '/6'
        When method GET
        Then status 200

        * def id = get[0] response[?(@.contactId == 5)].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete course class tutors. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete Tutor with existing payslips

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Tutor has payslips and can not be deleted from this class"



    Scenario: (-) Delete not existing Tutor

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."