@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/reportOverlay'

    Background: Authorize first
        * call read('../../../signIn.feature')
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
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add new overlay by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
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

