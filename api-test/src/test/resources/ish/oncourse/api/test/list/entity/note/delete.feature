@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/note'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/note'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        


        
    Scenario: (+) Delete existing Note by admin

#       <----->  Add a new custom note for deleting and get its id:
        * def newNote = {"message":"delete note1","entityName":"Course"}

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

        * def noteId = get[0] response[?(@.message == 'delete note1')].id
        * print "noteId = " + noteId
#       <--->

        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200
        And match $ == []



    Scenario: (+) Delete existing Note by notadmin with access rights

#       <----->  Add a new custom note for deleting and get its id:
        * def newNote = {"message":"delete note2","entityName":"Course"}

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

        * def noteId = get[0] response[?(@.message == 'delete note2')].id
        * print "noteId = " + noteId

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath
        And param entityName = "Course"
        And param entityId = 2
        When method GET
        Then status 200
        And match $ == []



    Scenario: (-) Delete existing Note by notadmin without access rights

#       <----->  Add a new custom note for deleting and get its id:
        * def newNote = {"message":"delete note3","entityName":"Course"}

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

        * def noteId = get[0] response[?(@.message == 'delete note3')].id
        * print "noteId = " + noteId

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete note. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + noteId
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing Note

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

