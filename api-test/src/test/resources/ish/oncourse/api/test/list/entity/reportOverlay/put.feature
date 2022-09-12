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
