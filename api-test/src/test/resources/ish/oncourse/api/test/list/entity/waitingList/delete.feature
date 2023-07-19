@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/waitingList'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/waitingList'
        * def ishPathPlain = 'list/plain'
        * def ishPathDelete = '/list/plain/bulkDelete?entity=WaitingList'
        


        
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
        "tags":[221],
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

        * def deleteRequest =
        """
        {"ids": [#(id)],"search": "","filter": "","tagGroups": []}
        """
        Given path ishPathDelete
        And request deleteRequest
        When method POST
        Then status 204

#       <---> Verification of deleting:
        Given path ishPathPlain
        And param entity = 'WaitingList'
        And param columns = 'studentCount'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["20"]


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
        "tags":[221],
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
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        * def deleteRequest =
        """
        {"ids": [#(id)],"search": "","filter": "","tagGroups": []}
        """
        Given path ishPathDelete
        And request deleteRequest
        When method POST
        Then status 403
        And match $.errorMessage == "Only users with admin rights can do it. Please contact your administrator"

#       <---->  Scenario have been finished. Now change back permissions and delete created entity:
        * configure headers = { Authorization: 'admin'}


        * def deleteRequest =
        """
        {"ids": [#(id)],"search": "","filter": "","tagGroups": []}
        """
        Given path ishPathDelete
        And request deleteRequest
        When method POST
        Then status 204



    Scenario: (-) Delete NOT existing WaitingList

        * def deleteRequest =
        """
        {"ids": [99999],"search": "","filter": "","tagGroups": []}
        """
        Given path ishPathDelete
        And request deleteRequest
        When method POST
        Then status 400
        And match response.errorMessage == "Record with id 99999 not found"
