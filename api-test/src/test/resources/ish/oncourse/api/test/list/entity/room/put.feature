@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/room'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/room'
        * def ishPathList = 'list'
        
        


    Scenario: (+) Update room by admin

#       <----->  Add a new room for updating and define id:
        * def newRoom =
        """
        {
        "name":"testRoom100",
        "seatedCapacity":25,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "tags":[{"id":212,"name":"1","status":"Private","system":false,"urlPath":null,"content":null,"weight":null,"taggedRecordsCount":0,"childrenCount":0,"requirements":[],"childTags":[]}],
        "documents":[],
        "rules":[{"id":null,"description":"test","repeatEnd":"never","repeat":"month","repeatEndAfter":0,"startDateTime":"2020-02-01T08:59:00.000Z","endDateTime":"2020-02-15T08:59:00.000Z"}]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom100","site1","25"])].id
#       <--->

        * def roomToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testRoom100_upd",
        "seatedCapacity":50,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "tags":[{"id":213,"name":"2","status":"Private","system":false,"urlPath":null,"content":null,"weight":null,"taggedRecordsCount":0,"childrenCount":0,"requirements":[],"childTags":[]}],
        "documents":[],
        "rules":[{"id":null,"description":"testUPD","startDate":"2020-02-03","endDate":"2020-02-22","startDateTime":null,"endDateTime":null,"repeat":"week","repeatEnd":"after","repeatEndAfter":"22","repeatOn":null}]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"testRoom100_upd",
        "seatedCapacity":50,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "tags":[{"id":213,"name":"2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null, "modified":null,"requirements":[],"childTags":[]}],
        "documents":[],
        "rules":[{"id":"#ignore","description":"testUPD","startDate":"2020-02-03","endDate":"2020-02-22","startDateTime":null,"endDateTime":null,"repeat":"week","repeatEnd":"after","repeatEndAfter":22,"repeatOn":null,"created":"#ignore","modified":"#ignore"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update room to max allowed length values

#       <----->  Add a new room for updating and define id:
        * def newRoom =
        """
        {
        "name":"testRoom101",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom101","site1","5"])].id
#       <--->

        * def roomToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testRoom_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150",
        "seatedCapacity":1234567890,
        "siteId":200,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.name == "testRoom_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150"
        And match $.seatedCapacity == 1234567890
        And match $.siteId == 200
        And match $.directions == "someDirections_upd"
        And match $.facilities == "someFacilities_upd"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update room by notadmin with access rights

#       <----->  Add a new room for updating and define id:
        * def newRoom =
        """
        {
        "name":"testRoom104",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom104","site1","5"])].id

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
        * def roomToUpdate =
        """
        {
        "name":"testRoom104_upd",
        "seatedCapacity":15,
        "siteId":200,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.name == "testRoom104_upd"
        And match $.seatedCapacity == 15
        And match $.siteId == 200
        And match $.directions == "someDirections_upd"
        And match $.facilities == "someFacilities_upd"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update room by notadmin without access rights

#       <----->  Add a new room for updating and define id:
        * def newRoom =
        """
        {
        "name":"testRoom105",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom105","site1","5"])].id

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

        * def roomToUpdate =
        """
        {
        "name":"testRoom105_upd",
        "seatedCapacity":52,
        "siteId":200,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit room. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update room required fields to empty

#       <----->  Add a new room for updating and define id:
        * def newRoom =
        """
        {
        "name":"testRoom101",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom101","site1","5"])].id

#       <--->  Update room to empty Name:
        * def roomToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "seatedCapacity":50,
        "siteId":200,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Update room to empty Seated capacity:
        * def roomToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testRoom100_upd",
        "seatedCapacity":null,
        "siteId":200,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Seated capacity is required."

 #       <--->  Scenario have been finished. Now find and remove created object from DB:
         * print "Scenario have been finished. Now find and remove created object from DB"

         Given path ishPath + '/' + id
         When method DELETE
         Then status 204



    Scenario: (-) Update room to not unique Name

#       <----->  Add a new room for updating and define id:
        * def newRoom =
        """
        {
        "name":"testRoom101",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom101","site1","5"])].id

#       <--->  Update room to not existent Name:
        * def roomToUpdate =
        """
        {
        "id":"#(id)",
        "name":"room1",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The name of the room must be unique within the site."

 #       <--->  Scenario have been finished. Now find and remove created object from DB:
         * print "Scenario have been finished. Now find and remove created object from DB"

         Given path ishPath + '/' + id
         When method DELETE
         Then status 204



    Scenario: (-) Update 'name' field length to out of range

#       <----->  Add a new entity to update and define id:
        * def newRoom =
        """
        {
        "name":"testRoom102",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["testRoom102","site1","5"])].id

#       <--->  Update Name to >150 symbols:
        * def roomToUpdate =
        """
        {
        "id":"#(id)",
        "name":"testRoom_A2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A151",
        "seatedCapacity":5,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath + '/' + id
        And request roomToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name can't be more than 150 chars."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update not existing room

        * def roomToUpdate =
        """
        {
        "id":99999,
        "name":"testRoom100_upd",
        "seatedCapacity":50,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections_upd",
        "facilities":"someFacilities_upd",
        "tags":[{"id":213,"name":"2","status":"Private","system":false,"urlPath":null,"content":null,"weight":null,"taggedRecordsCount":0,"childrenCount":0,"requirements":[],"childTags":[]}],
        "documents":[],
        "rules":[]
        }
        """

        Given path ishPath + '/99999'
        And request roomToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Room with id:99999 doesn't exist"



#    Scenario: (-) Update 'seatedCapacity' field length to out of range
#
##       <----->  Add a new entity to update and define id:
#        * def newRoom =
#        """
#        {
#        "name":"testRoom103",
#        "seatedCapacity":5,
#        "siteId":201,
#        "kioskUrl":null,
#        "directions":"someDirections",
#        "facilities":"someFacilities",
#        "rules":[]
#        }
#        """
#
#        Given path ishPath
#        And request newRoom
#        When method POST
#        Then status 204
#
#        Given path ishPathList
#        And param entity = 'Room'
#        When method GET
#        Then status 200
#
#        * def id = get[0] response.rows[?(@.values == ["testRoom103","site1","5"])].id
#
##       <--->  Update Seated capacity to >10 symbols:
#        * def roomToUpdate =
#        """
#        {
#        "id":"#(id)",
#        "name":"testRoom_103",
#        "seatedCapacity":12345678901,
#        "siteId":201,
#        "kioskUrl":null,
#        "directions":"someDirections",
#        "facilities":"someFacilities",
#        "rules":[]
#        }
#        """
#
#        Given path ishPath + '/' + id
#        And request roomToUpdate
#        When method PUT
#        Then status 400
#        And match $.errorMessage == "Seated capacity can't be more than 50 chars."
#
# #       <--->  Scenario have been finished. Now find and remove created object from DB:
#         Given path ishPath + '/' + id
#         When method DELETE
#         Then status 204
