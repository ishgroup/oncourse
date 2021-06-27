@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/tutor'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create Tutor by admin

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

#       <---> Verification:
        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ contains
            """
            [
                {"confirmedOn":"2018-01-01","classId":6,"tutorName":"tutor2","contactId":5,"roleId":1,"isInPublicity":true,"roleName":"Tutor","id":#ignore}       
            ]
            """
#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Tutor by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def newTutor =
            """
            {
            "classId":6,
            "contactId":5,
            "roleId":2,
            "confirmedOn":null,
            "isInPublicity":false
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

#       <---> Verification:
        Given path ishPath + '/6'
        When method GET
        Then status 200

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Tutor by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def newTutor =
            """
            {
            "classId":6,
            "contactId":5,
            "roleId":2,
            "confirmedOn":null,
            "isInPublicity":false
            }
            """

        Given path ishPath
        And request newTutor
        When method POST
        Then status 403
        