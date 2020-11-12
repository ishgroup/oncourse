@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/courseClass/tutor'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update existing Tutor by admin

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
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

        * def id = get[0] response[?(@.confirmedOn == '2018-01-01')].id
        * print "id = " + id
#       <--->

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":6,
            "contactId":1,
            "roleId":1,
            "confirmedOn":"2018-02-01",
            "isInPublicity":false
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 204

#       <---> Verification:
        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ contains [{"id":"#(~~id)","classId":6,"contactId":1,"roleId":1,"tutorName":"tutor1","roleName":"Lecturer","confirmedOn":"2018-02-01","isInPublicity":false}]

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update existing Tutor by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
              "roleId":1,
              "confirmedOn":"2018-01-10",
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

        * def id = get[0] response[?(@.confirmedOn == '2018-01-10')].id
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

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":6,
            "contactId":1,
            "roleId":1,
            "confirmedOn":"2018-02-11",
            "isInPublicity":false
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 204

#       <---> Verification:
        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ contains [{"id":"#(~~id)","classId":6,"contactId":1,"roleId":1,"tutorName":"tutor1","roleName":"Lecturer","confirmedOn":"2018-02-11","isInPublicity":false}]

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update existing Tutor by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
              "roleId":1,
              "confirmedOn":"2018-01-20",
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

        * def id = get[0] response[?(@.confirmedOn == '2018-01-20')].id
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

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":6,
            "contactId":1,
            "roleId":1,
            "confirmedOn":"2018-02-21",
            "isInPublicity":false
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit course class tutors. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update classId for existing Tutor

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
              "roleId":2,
              "confirmedOn":"2018-01-02",
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

        * def id = get[0] response[?(@.confirmedOn == '2018-01-02')].id
        * print "id = " + id
#       <--->

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":7,
            "contactId":1,
            "roleId":2,
            "confirmedOn":"2018-01-02",
            "isInPublicity":true
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Class can not be changed"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update contactId for existing Tutor

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
              "roleId":3,
              "confirmedOn":"2018-01-03",
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

        * def id = get[0] response[?(@.confirmedOn == '2018-01-03')].id
        * print "id = " + id
#       <--->

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":6,
            "contactId":5,
            "roleId":3,
            "confirmedOn":"2018-01-03",
            "isInPublicity":true
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Tutor can not be changed"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update roleId for existing Tutor

#       <----->  Add a new entity for deleting and get id:
        * def newTutor =
              """
              {
              "classId":6,
              "contactId":1,
              "roleId":4,
              "confirmedOn":"2018-01-04",
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

        * def id = get[0] response[?(@.confirmedOn == '2018-01-04')].id
        * print "id = " + id
#       <--->

        * def tutorToUpdate =
            """
            {
            "id":"#(id)",
            "classId":6,
            "contactId":1,
            "roleId":5,
            "confirmedOn":"2018-01-04",
            "isInPublicity":true
            }
            """

        Given path ishPath + '/' + id
        And request tutorToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Defined tutor role can not be changed"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Tutor

        * def tutorToUpdate =
            """
            {
            "id":99999,
            "classId":6,
            "contactId":1,
            "roleId":5,
            "confirmedOn":"2018-01-05",
            "isInPublicity":true
            }
            """

        Given path ishPath + '/99999'
        And request tutorToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."