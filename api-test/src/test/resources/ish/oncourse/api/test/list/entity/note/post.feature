@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/note'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/note'
        * def ishPathCourse = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create new note by admin

        * def newNote = {"message":"create note1","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200
        And match $ contains [{"id":"#number","created":"#ignore","modified":"#ignore","message":"create note1","createdBy":"onCourse Administrator","modifiedBy":null,"entityName":"Course","entityId":2}]

        * def noteId = get[0] response[?(@.message == 'create note1')].id
        * print "noteId = " + noteId

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (+) Create new note by notadmin with access rights

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * def newNote = {"message":"create note2","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200
        And match $ contains [{"id":"#number","created":"#ignore","modified":"#ignore","message":"create note2","createdBy":"UserWithRightsDelete UserWithRightsDelete","modifiedBy":null,"entityName":"Course","entityId":2}]

        * def noteId = get[0] response[?(@.message == 'create note2')].id
        * print "noteId = " + noteId

#       <---->  Scenario have been finished. Now delete created entity from db:
        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (-) Create new note by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        * def newNote = {"message":"create note3","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create note. Please contact your administrator"



    Scenario: (-) Create new note with empty message

        * def newNote = {"message":"","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 400
        And match $.errorMessage == "Text is required."


