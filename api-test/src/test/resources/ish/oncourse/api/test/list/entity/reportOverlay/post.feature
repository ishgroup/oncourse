@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/reportOverlay'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/reportOverlay'
        * def ishPathList = 'list/plain'
        * def ishPathLogin = 'login'
        * def ishPathControl = 'control'
        



    Scenario: (+) Add new overlay by admin

        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay1'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay1'])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "name":"newOverlay1",
        "preview":"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"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add new overlay by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay33'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay33'])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ == {"id":"#(~~id)","name":"newOverlay33","preview":"#present"}

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add new overlay by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay33'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create background. Please contact your administrator"



    Scenario: (-) Add new overlay with existing name

#       <---> Add new entity and get it id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay2'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay2'])].id
        * print "id = " + id
#       <--->

        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay2'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 400
        And match $.errorMessage == "Background with the same name is already exists."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add new overlay with empty name

        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = ''
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Add new overlay with name length >100 symbols

        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 characters."

