@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/definedTutorRole'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/definedTutorRole'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create Tutor Role by admin

        * def newTutorRole =
        """
        {
        "name":"tutor role post01",
        "description":"post01",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"},
            {"type":"Per session","validFrom":"2014-09-15","rate":12.00,"oncostRate":0.1000,"notes":"some notes"},
            {"type":"Per enrolment","validFrom":"2014-09-15","rate":13.00,"oncostRate":0.1000,"notes":"some notes"},
            {"type":"Per unit","validFrom":"2014-09-15","rate":14.00,"oncostRate":0.1000,"notes":"some notes"},
            {"type":"Per timetabled hour","validFrom":"2014-09-15","rate":15.00,"oncostRate":0.1000,"notes":"some notes"},
            {"type":"Per student contact hour","validFrom":"2014-09-15","rate":16.00,"oncostRate":0.1000,"notes":"some notes"}
            ]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'DefinedTutorRole'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["tutor role post01"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"tutor role post01",
        "description":"post01",
        "active":true,
        "payRates":"#ignore",
        }
        """

        And match $.payRates contains
        """
        [
        {"id":"#number","type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"},
        {"id":"#number","type":"Per timetabled hour","validFrom":"2014-09-15","rate":15.00,"oncostRate":0.1000,"notes":"some notes"},
        {"id":"#number","type":"Per unit","validFrom":"2014-09-15","rate":14.00,"oncostRate":0.1000,"notes":"some notes"},
        {"id":"#number","type":"Per enrolment","validFrom":"2014-09-15","rate":13.00,"oncostRate":0.1000,"notes":"some notes"},
        {"id":"#number","type":"Per student contact hour","validFrom":"2014-09-15","rate":16.00,"oncostRate":0.1000,"notes":"some notes"},
        {"id":"#number","type":"Per session","validFrom":"2014-09-15","rate":12.00,"oncostRate":0.1000,"notes":"some notes"}
        ]
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create Tutor Role by notadmin

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

        * def newTutorRole =
        """
        {
        "name":"tutor role post02",
        "description":"post02",
        "active":true,
        "payRates":
            [
            {"type":"Per student contact hour","validFrom":"2014-09-10","rate":110.00,"oncostRate":0.0100,"notes":"some notes"},
            {"type":"Per session","validFrom":"2014-09-15","rate":12.00,"oncostRate":0.1000,"notes":"some notes"}
            ]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 403



    Scenario: (-) Create new Tutor Role with empty Name

        * def newTutorRole =
        """
        {
        "name":"",
        "description":"post04",
        "active":true,
        "payRates":[{"type":"Per student contact hour","validFrom":"2014-09-01","rate":10.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create new Tutor Role with empty description

        * def newTutorRole =
        """
        {
        "name":"tutor role post05",
        "description":"",
        "active":true,
        "payRates":[{"type":"Per student contact hour","validFrom":"2014-09-01","rate":10.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Description cannot be empty."



    Scenario: (-) Create new Tutor Role with empty Rate

        * def newTutorRole =
        """
        {
        "name":"tutor role post06",
        "description":"post06",
        "active":true,
        "payRates":[{"type":"Per student contact hour","validFrom":"2014-09-01","rate":null,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Rate is required."



    Scenario: (-) Create new Tutor Role with not existing type

        * def newTutorRole =
        """
        {
        "name":"tutor role post07",
        "description":"post07",
        "active":true,
        "payRates":[{"type":"not existing type","validFrom":"2014-09-01","rate":10.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Type is required."



    Scenario: (-) Create new Tutor Role with empty validFrom

        * def newTutorRole =
        """
        {
        "name":"tutor role post07",
        "description":"post07",
        "active":true,
        "payRates":[{"type":"Per unit","validFrom":"","rate":10.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Valid from is required."



    Scenario: (-) Create new Tutor Role with Name length >64 symbols

        * def newTutorRole =
        """
        {
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66",
        "description":"post08",
        "active":true,
        "payRates":[{"type":"Per unit","validFrom":"2014-09-25","rate":10.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 64 chars."



    Scenario: (-) Create new Tutor Role with description length >128 symbols

        * def newTutorRole =
        """
        {
        "name":"tutor role post08",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1",
        "active":true,
        "payRates":[{"type":"Per unit","validFrom":"2014-09-25","rate":10.00,"oncostRate":0.1000,"notes":"some notes"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Description cannot be more than 128 chars."



    Scenario: (-) Create new Tutor Role with notes length >128 symbols

        * def newTutorRole =
        """
        {
        "name":"tutor role post09",
        "description":"post09",
        "active":true,
        "payRates":[{"type":"Per unit","validFrom":"2014-09-25","rate":10.00,"oncostRate":0.1000,"notes":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1"}]
        }
        """

        Given path ishPath
        And request newTutorRole
        When method POST
        Then status 400
        And match $.errorMessage == "Notes cannot be more than 128 chars."
