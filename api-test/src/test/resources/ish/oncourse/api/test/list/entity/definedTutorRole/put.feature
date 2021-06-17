@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/definedTutorRole'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/definedTutorRole'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update Tutor Role by admin

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put01",
        "description":"put01",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put01"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

        * def tutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put01 UPD",
        "description":"put01 UPD",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"Per unit","validFrom":"2015-09-15","rate":120.00,"oncostRate":0.1500,"notes":"some notes upd"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request tutorRoleToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"tutor role put01 UPD",
        "description":"put01 UPD",
        "active":false,
        "payRates":[{"id":"#number","type":"Per unit","validFrom":"2015-09-15","rate":120.00,"oncostRate":0.1500,"notes":"some notes upd"}]
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role by notadmin

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

       * def newTutorRoleToUpdate = {"id":0,"name":"Tutor","description":"Tutor","active":false,"payRates":[]}

        Given path ishPath + '/0'
        And request newTutorRoleToUpdate
        When method PUT
        Then status 403



    Scenario: (-) Update Tutor Role to empty name

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put03",
        "description":"put03",
        "active":true,
        "payRates":[]
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put03"])].id
        * print "id = " + id
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "description":"put03_UPD",
        "active":false,
        "payRates":[]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role to empty description

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put04",
        "description":"put04",
        "active":true,
        "payRates":[]
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put04"])].id
        * print "id = " + id
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put04_UPD",
        "description":"",
        "active":false,
        "payRates":[]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Description cannot be empty."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role rate to empty rate

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put05",
        "description":"put05",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put05"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put05_UPD",
        "description":"put05_UPD",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"Fixed","validFrom":"2015-09-15","rate":null,"oncostRate":0.1500,"notes":"some notes upd"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Rate is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role rate to empty validFrom

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put06",
        "description":"put06",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put06"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put06_UPD",
        "description":"put06_UPD",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"Fixed","validFrom":"","rate":12.00,"oncostRate":0.1500,"notes":"some notes upd"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Valid from is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role name to >64 symbols

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put07",
        "description":"put07",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put07"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66",
        "description":"put07_UPD",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"Fixed","validFrom":"2015-09-15","rate":120.00,"oncostRate":0.1500,"notes":"some notes upd"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name cannot be more than 64 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role description to >128 symbols

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put08",
        "description":"put08",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put08"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put08_UPD",
        "description":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"Fixed","validFrom":"2015-09-15","rate":120.00,"oncostRate":0.1500,"notes":"some notes upd"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Description cannot be more than 128 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role notes to >128 symbols

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put09",
        "description":"put09",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put09"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put09_UPD",
        "description":"put09_UPD",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"Fixed","validFrom":"2015-09-15","rate":120.00,"oncostRate":0.1500,"notes":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A1"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Notes cannot be more than 128 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Tutor Role type to not existing

#       <----->  Add a new entity to update and define its id:
        * def newTutorRole =
        """
        {
        "name":"tutor role put10",
        "description":"put10",
        "active":true,
        "payRates":
            [
            {"type":"Fixed","validFrom":"2014-09-15","rate":11.00,"oncostRate":0.1000,"notes":"some notes"}
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

        * def id = get[0] response.rows[?(@.values == ["tutor role put10"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def payRatesId = get[0] response.payRates[0].id
        * print "payRatesId = " + payRatesId
#       <--->

       * def newTutorRoleToUpdate =
        """
        {
        "id":"#(id)",
        "name":"tutor role put10_UPD",
        "description":"put10_UPD",
        "active":false,
        "payRates":
            [
            {"id":"#(payRatesId)","type":"not existing","validFrom":"2015-09-15","rate":120.00,"oncostRate":0.1500,"notes":"some notes upd"}
            ]
        }
        """

        Given path ishPath + '/' + id
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Type is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Tutor Role

       * def newTutorRoleToUpdate =
        """
        {
        "id":99999,
        "name":"tutor role put08_UPD",
        "description":"put08_UPD",
        "active":false,
        "payRates":[]
        }
        """

        Given path ishPath + '/99999'
        And request newTutorRoleToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."




