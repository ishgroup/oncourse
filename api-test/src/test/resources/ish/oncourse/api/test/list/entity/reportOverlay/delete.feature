@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/reportOverlay'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/reportOverlay'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


        
    Scenario: (+) Delete Overlay by admin

#       <----->  Add a new entity for deleting and get id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay20'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay20'])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

##       <---> Verification of deleting
#        Given path ishPath + '/' + id
#        When method GET
#        Then status 400
#        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete Overlay by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay21'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay21'])].id
        * print "id = " + id

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

##       <---> Verification of deleting
#        Given path ishPath + '/' + id
#        When method GET
#        Then status 400
#        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete Overlay by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay22'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay22'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete background. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing Report

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400