@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/waitingList'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/waitingList'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        
    Scenario: (+) Delete existing WaitingList by admin

#       <----->  Add a new entity for deleting and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 20",
        "studentNotes":null,
        "studentCount":20,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["20"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["20"]



    Scenario: (+) Delete existing WaitingList by notadmin with rights

#       <----->  Add a new entity for deleting and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 21",
        "studentNotes":null,
        "studentCount":21,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["21"])].id
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

#       <---> Verification deleting:
        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["21"]



    Scenario: (-) Delete existing WaitingList by notadmin without rights

#       <----->  Add a new entity for deleting and define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"Some notes 22",
        "studentNotes":null,
        "studentCount":22,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["22"])].id
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
        And match $.errorMessage == "Sorry, you have no permissions to delete waitingList. Please contact your administrator"

#       <---->  Scenario have been finished. Now change back permissions and delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing WaitingList

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "WaitingList with id:99999 doesn't exist"
