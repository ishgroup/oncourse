@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/room'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/room'
        * def ishPathList = 'list'
        



    Scenario: (+) Create room by admin

        * def newRoom =
        """
        {
        "name":"testRoom1",
        "seatedCapacity":25,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "tags":[{"id":212,"name":"1","status":"Private","system":false,"urlPath":null,"content":null,"weight":null,"taggedRecordsCount":0,"childrenCount":0,"requirements":[],"childTags":[]}],
        "documents":[],
        "rules":[{"id":null,"startDate":"2020-02-01","endDate":"2020-02-05","repeatEnd":"after","repeat":"day","repeatEndAfter":"5","startDateTime":null,"endDateTime":null}]}
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["testRoom1","site1","25"]

        * def id = get[0] response.rows[?(@.values == ["testRoom1","site1","25"])].id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"testRoom1",
        "seatedCapacity":25,
        "siteId":"#number",
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "created":"#ignore",
        "modified":"#ignore",
        "tags":
            [{
            "id":212,
            "name":"1",
            "status":null,
            "system":null,
            "urlPath":null,
            "content":null,
            "color":null,
            "weight":null,
            "taggedRecordsCount":null,
            "childrenCount":null,
            "created":null,
            "modified":null,
            "requirements":[],
            "childTags":[]
            }],
        "documents":[],
        "rules":[{"id":"#ignore","description":null,"startDate":"2020-02-01","endDate":"2020-02-05","startDateTime":null,"endDateTime":null,"repeat":"day","repeatEnd":"after","repeatEndAfter":5,"repeatOn":null,"created":"#ignore","modified":"#ignore"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create room with max allowed fields length for name and seatedCapacity

        * def newRoom =
        """
        {
        "name":"testRoom_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150",
        "seatedCapacity":1234567890,
        "siteId":201,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
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
        And match $.rows[*].values[*] contains ["testRoom_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150","site1","1234567890"]

        * def id = get[0] response.rows[?(@.values == ["testRoom_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150","site1","1234567890"])].id

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create room by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def newRoom =
        """
        {
        "name":"testRoom2",
        "seatedCapacity":25,
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
        And match $.rows[*].values[*] contains ["testRoom2","site1","25"]

        * def id = get[0] response.rows[?(@.values == ["testRoom2","site1","25"])].id

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create room by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def newRoom =
        """
        {
        "name":"testRoom3",
        "seatedCapacity":25,
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
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create room. Please contact your administrator"



    Scenario: (-) Create room with empty Name

        * def newRoom =
        """
        {
        "name":"",
        "seatedCapacity":25,
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
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create room with existing 'Name' within the site

        * def newRoom =
        """
        {
        "name":"room1",
        "seatedCapacity":25,
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
        Then status 400
        And match $.errorMessage == "The name of the room must be unique within the site."



    Scenario: (-) Create room with empty Seated capacity

        * def newRoom =
        """
        {
        "name":"testRoom4",
        "seatedCapacity":null,
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
        Then status 400
        And match $.errorMessage == "Seated capacity is required."



#    Scenario: (-) Create room with not numeric Seated capacity
#
#        * def newRoom =
#        """
#        {
#        "name":"testRoom4",
#        "seatedCapacity":"abc",
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
#        Then status 400
#        And match $.errorMessage == ""



    Scenario: (-) Create room with empty Site

        * def newRoom =
        """
        {
        "name":"testRoom5",
        "seatedCapacity":25,
        "siteId":null,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 400
        And match $.errorMessage == "Site is required."



    Scenario: (-) Create room with nonexistent Site

        * def newRoom =
        """
        {
        "name":"testRoom6",
        "seatedCapacity":25,
        "siteId":999,
        "kioskUrl":null,
        "directions":"someDirections",
        "facilities":"someFacilities",
        "rules":[]
        }
        """

        Given path ishPath
        And request newRoom
        When method POST
        Then status 400
        And match $.errorMessage == "Can't bind room to nonexistent site"



    Scenario: (-) Create room with Name >150 symbols

        * def newRoom =
        """
        {
        "name":"testRoom_A2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A151",
        "seatedCapacity":25,
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
        Then status 400
        And match $.errorMessage == "Name can't be more than 150 chars."

