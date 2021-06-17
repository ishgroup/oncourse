@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/note'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/note'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Update Note by admin

#       <----->  Add a new entity to update and define id:
        * def newNote = {"message":"update note1","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 204

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200

        * def noteId = get[0] response[?(@.message == 'update note1')].id
        * print "noteId = " + noteId
#       <--->

        * def noteToUpdate = {"id":"#(noteId)","message":"update note1UPD","entityName":"Course","entityId":2}

        Given path ishPath + '/' + noteId
        And request noteToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200
        And match $ contains [{"id":"#(~~noteId)","created":"#ignore","modified":"#ignore","message":"update note1UPD","createdBy":"onCourse Administrator","modifiedBy":"onCourse Administrator","entityName":"Course","entityId":2}]

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (+) Update Note by notadmin with access rights

#       <----->  Add a new entity to update and define id:
        * def newNote = {"message":"update note2","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 204

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200

        * def noteId = get[0] response[?(@.message == 'update note2')].id
        * print "noteId = " + noteId

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

        * def noteToUpdate = {"id":"#(noteId)","message":"update note2UPD","entityName":"Course","entityId":2}

        Given path ishPath + '/' + noteId
        And request noteToUpdate
        When method PUT
        Then status 204

#       <---> Assertion:
        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200
        And match $ contains [{"id":"#(~~noteId)","created":"#ignore","modified":"#ignore","message":"update note2UPD","createdBy":"onCourse Administrator","modifiedBy":"UserWithRightsDelete UserWithRightsDelete","entityName":"Course","entityId":2}]

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (-) Update Note by notadmin without access rights

#       <----->  Add a new entity to update and define id:
        * def newNote = {"message":"update note3","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 204

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200

        * def noteId = get[0] response[?(@.message == 'update note3')].id
        * print "noteId = " + noteId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def noteToUpdate = {"id":"#(noteId)","message":"update note3UPD","entityName":"Course","entityId":2}

        Given path ishPath + '/' + noteId
        And request noteToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update note. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (-) Update Note 'message' field to empty

#       <----->  Add a new entity to update and define id:
        * def newNote = {"message":"update note4","entityName":"Course"}

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        And request newNote
        When method POST
        Then status 204

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200

        * def noteId = get[0] response[?(@.message == 'update note4')].id
        * print "noteId = " + noteId
#       <--->

        * def noteToUpdate = {"id":"#(noteId)","message":"","entityName":"Course","entityId":2}

        Given path ishPath + '/' + noteId
        And request noteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Text is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Note

        * def noteToUpdate = {"id":"99999","message":"update note99999","entityName":"Course","entityId":2}

        Given path ishPath + '/99999'
        And request noteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."