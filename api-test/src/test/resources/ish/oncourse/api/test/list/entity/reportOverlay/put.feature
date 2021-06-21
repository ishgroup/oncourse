@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/reportOverlay'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/reportOverlay'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update Overlay by admin

#       <----->  Add a new entity to update and define its id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay51'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay51'])].id
        * print "id = " + id
#       <--->

        * def someStream = read('newOverlay1.pdf')

        Given path ishPath + '/' + id
        And param fileName = "newOverlay51UPD"
        And header Content-Type = 'application/pdf'
        And request someStream
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "name":"newOverlay51UPD",
        "preview":"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"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Overlay by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay52'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay52'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def someStream = read('newOverlay1.pdf')

        Given path ishPath + '/' + id
        And param fileName = "newOverlay52UPD"
        And header Content-Type = 'application/pdf'
        And request someStream
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "name":"newOverlay52UPD",
        "preview":"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"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Overlay by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay53'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay53'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def someStream = read('newOverlay1.pdf')

        Given path ishPath + '/' + id
        And param fileName = "newOverlay53UPD"
        And header Content-Type = 'application/pdf'
        And request someStream
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update background. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Overlay to empty Name

#       <----->  Add a new entity to update and define its id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay80'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay80'])].id
        * print "id = " + id

#       <--->  Update report to empty Name:
         * def someStream = read('newOverlay.pdf')

        Given path ishPath + '/' + id
        And param fileName = ""
        And header Content-Type = 'application/pdf'
        And request someStream
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Overlay to existing Name

#       <----->  Add a new entity to update and define its id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay81'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        * def someStream = read('newOverlay1.pdf')

        Given path ishPath
        And param fileName = 'newOverlay81a'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id1 = get[0] response.rows[?(@.values == ['newOverlay81'])].id
        * def id2 = get[0] response.rows[?(@.values == ['newOverlay81a'])].id
        * print "id1 = " + id1
        * print "id2 = " + id2
#       <--->

         * def someStream = read('newOverlay1.pdf')

        Given path ishPath + '/' + id2
        And param fileName = "newOverlay81"
        And header Content-Type = 'application/pdf'
        And request {}
        When method PUT
        Then status 400
        And match $.errorMessage == "Background with the same name is already exists."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPath + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Update Overlay Name to length >100 symbols

#       <----->  Add a new entity to update and define its id:
        * def someStream = read('newOverlay.pdf')

        Given path ishPath
        And param fileName = 'newOverlay89'
        And header Content-Type = 'application/pdf'
        And request someStream
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ReportOverlay'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['newOverlay89'])].id
        * print "id = " + id
#       <--->

         * def someStream = read('newOverlay1.pdf')

        Given path ishPath + '/' + id
        And param fileName = "A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1"
        And header Content-Type = 'application/pdf'
        And request someStream
        When method PUT
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 characters."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Overlay

         * def someStream = read('newOverlay1.pdf')

        Given path ishPath + '/99999'
        And param fileName = "newOverlay83"
        And header Content-Type = 'application/pdf'
        And request someStream
        When method PUT
        Then status 400
        And match $.errorMessage == "Report overlay with id: 99999 doesn't exist"
