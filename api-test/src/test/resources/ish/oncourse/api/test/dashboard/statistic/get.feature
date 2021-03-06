@parallel=false
Feature: Main feature for all GET requests with path 'dashboard/statistic'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'dashboard/statistic'
        * def ishPathWaitingList = 'list/entity/waitingList'
        * def ishPathPlain = 'list/plain'
        * def ishPathLogin = 'login'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get statistic (Waiting list) by admin

#      <---> Create Waiting list ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"WL for testing statistic",
        "studentNotes":"should be null after creation",
        "studentCount":99,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPathWaitingList
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["99"])].id
        * print "id = " + id
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match $.latestWaitingLists == [{"title":"Course2","info":"1","link":"/waitingList?search=course.id=2"}]

#       <---> Scenario have been finished. Now find and remove created Waiting List from DB:
        Given path ishPathWaitingList + '/' + id
        When method DELETE
        Then status 204


    Scenario: (+) Get statistic (Waiting list) by notadmin

#      <---> Create Waiting list ang define id:
        * def newWaitingList =
        """
        {
        "privateNotes":"WL for testing statistic",
        "studentNotes":"should be null after creation",
        "studentCount":991,
        "contactId":3,
        "courseId":2,
        "tags":[{"id":221}],
        "sites":[{"id":200}],
        "customFields":{}
        }
        """

        Given path ishPathWaitingList
        And request newWaitingList
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["991"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match $.latestWaitingLists == [{"title":"Course2","info":"1","link":"/waitingList?search=course.id=2"}]

#       <---> Scenario have been finished. Now find and remove created Waiting List from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathWaitingList + '/' + id
        When method DELETE
        Then status 204


