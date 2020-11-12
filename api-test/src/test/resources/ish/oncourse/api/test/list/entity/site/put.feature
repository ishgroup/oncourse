@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/site'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/site'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'

        * def newSite =
        """
        {
        "isAdministrationCentre":false,
        "isVirtual":false,
        "isShownOnWeb":false,
        "kioskUrl":null,
        "name":"someSite100",
        "street":"anyStreet",
        "suburb":"Adelaide",
        "state":"SA",
        "postcode":"5000",
        "country":{"id":1,"name":"Australia"},
        "timezone":"Australia/Sydney",
        "longitude":138.60569150,
        "latitude":-34.91638480,
        "drivingDirections":"someDrivingDirections",
        "publicTransportDirections":"somePublicTransportDirections",
        "specialInstructions":"someSpecialInstructions",
        "tags":[],
        "rooms":[],
        "documents":[],
        "rules":[]
        }
        """



    Scenario: (+) Update site by admin

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id
#       <--->

        * def siteToUpdate =
            """
            {"id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"someSite100_upd",
            "street":"anyStreet_upd",
            "suburb":"Adelaide",
            "state":"SA",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/South",
            "longitude":137.6007456,
            "latitude":-33.9284989,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[{"id":208,"name":"1","status":"Private","system":false,"urlPath":null,"content":null,"weight":null,"taggedRecordsCount":0,"childrenCount":0,"requirements":[],"childTags":[]}],
            "rooms":[{"siteId":"#(id)","name":"room1","seatedCapacity":"75"}],
            "documents":[],
            "rules":[{"id":null,"description":"fff","startDate":"2020-01-01","endDate":"2020-02-29","repeatEnd":"never","repeat":"hour","repeatEndAfter":0,"startDateTime":null,"endDateTime":null}]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
            """
            {
            "id":"#number",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"someSite100_upd",
            "street":"anyStreet_upd",
            "suburb":"Adelaide",
            "state":"SA",
            "postcode":"5001",
            "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
            "timezone":"Australia/South",
            "longitude":137.60074560,
            "latitude":-33.92849890,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[{"id":208,"name":"1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
            "rooms":[{"id":"#number","name":"room1","seatedCapacity":75,"siteId":"#number","siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}],
            "documents":[],
            "rules":[{"id":"#ignore","description":"fff","startDate":"2020-01-01","endDate":"2020-02-29","startDateTime":null,"endDateTime":null,"repeat":"hour","repeatEnd":"never","repeatEndAfter":null,"repeatOn":null,"created":"#ignore","modified":"#ignore"}],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update site to max allowed length values

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id
#       <--->

        * def siteToUpdate =
            """
            {
            "id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"testSite_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150",
            "street":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "suburb":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51",
            "state":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51",
            "postcode":"1234567890",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/Sydney",
            "longitude":180.000000,
            "latitude":-90.000000,
            "drivingDirections":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "publicTransportDirections":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "specialInstructions":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
            """
            {
            "id":"#ignore",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"testSite_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150",
            "street":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "suburb":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51",
            "state":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51",
            "postcode":"1234567890",
            "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
            "timezone":"Australia/Sydney",
            "longitude":180.000000,
            "latitude":-90.000000,
            "drivingDirections":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "publicTransportDirections":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "specialInstructions":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update site by notadmin with rights

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id

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

        * def siteToUpdate =
            """
            {
            "id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"someSite100_upd",
            "street":"anyStreet_upd",
            "suburb":"Holroyd",
            "state":"NSW",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/Sydney",
            "longitude":150.948392,
            "latitude":-33.822474,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[{"id":208,"name":"1","status":"Private","system":false,"urlPath":null,"content":null,"weight":null,"taggedRecordsCount":0,"childrenCount":0,"requirements":[],"childTags":[]}],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
            """
            {
            "id":"#ignore",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"someSite100_upd",
            "street":"anyStreet_upd",
            "suburb":"Holroyd",
            "state":"NSW",
            "postcode":"5001",
            "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
            "timezone":"Australia/Sydney",
            "longitude":150.94839200,
            "latitude":-33.82247400,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[{"id":208,"name":"1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
            "rooms":[],
            "documents":[],
            "rules":[],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """

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



    Scenario: (-) Update site by notadmin without rights

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def siteToUpdate =
            """
            {
            "id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"someSite100_upd",
            "street":"anyStreet_upd",
            "suburb":"Holroyd",
            "state":"NSW",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/Sydney",
            "longitude":150.948392,
            "latitude":-33.822474,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit site. Please contact your administrator"

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



    Scenario: (-) Update site Name field to empty

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id
#       <--->

        * def siteToUpdate =
            """
            {
            "id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"",
            "street":"anyStreet_upd",
            "suburb":"Holroyd",
            "state":"NSW",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/Sydney",
            "longitude":150.948392,
            "latitude":-33.822474,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update site to not unique Name

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id
#       <--->

        * def siteToUpdate =
            """
            {
            "id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"site1",
            "street":"anyStreet_upd",
            "suburb":"Holroyd",
            "state":"NSW",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/Sydney",
            "longitude":150.948392,
            "latitude":-33.822474,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The name of the site must be unique."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update timezone to null

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id
#       <--->

        * def siteToUpdate =
            """
            {
            "id":"#(id)",
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"site111",
            "street":"anyStreet_upd",
            "suburb":"Holroyd",
            "state":"NSW",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":null,
            "longitude":150.948392,
            "latitude":-33.822474,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Timezone is required."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update site fields length to out of range

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["someSite100","Adelaide","5000","false"])].id

#       <--->  Update Name to >150 symbols:
        * def siteToUpdate = {"id":"#(id)","isAdministrationCentre":true,"isVirtual":false,"isShownOnWeb":true,"kioskUrl":null,"name":"testSite_A2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A151","street":"anyStreet_upd","suburb":"Holroyd","state":"NSW","postcode":"5001","country":{"id":1,"name":"Australia"},"timezone":"Australia/Sydney","longitude":150.948392,"latitude":-33.822474,"drivingDirections":"someDrivingDirections_upd","publicTransportDirections":"somePublicTransportDirections_upd","specialInstructions":"someSpecialInstructions_upd","tags":[],"rooms":[],"documents":[],"rules":[]}

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name can't be more than 150 chars."

#       <--->  Update Latitude to >180 degree:
        * def siteToUpdate = {"id":"#(id)","isAdministrationCentre":true,"isVirtual":false,"isShownOnWeb":true,"kioskUrl":null,"name":"someSite100_upd","street":"anyStreet_upd","suburb":"Holroyd","state":"NSW","postcode":"5001","country":{"id":1,"name":"Australia"},"timezone":"Australia/Sydney","longitude":150.948392,"latitude":90.00000080,"drivingDirections":"someDrivingDirections_upd","publicTransportDirections":"somePublicTransportDirections_upd","specialInstructions":"someSpecialInstructions_upd","tags":[],"rooms":[],"documents":[],"rules":[]}

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Invalid latitude value. Latitude must be between 90 and -90"

#       <--->  Update Longitude to >180 degree:
        * def siteToUpdate = {"id":"#(id)","isAdministrationCentre":true,"isVirtual":false,"isShownOnWeb":true,"kioskUrl":null,"name":"someSite100_upd","street":"anyStreet_upd","suburb":"Holroyd","state":"NSW","postcode":"5001","country":{"id":1,"name":"Australia"},"timezone":"Australia/Sydney","longitude":180.00000150,"latitude":-33.822474,"drivingDirections":"someDrivingDirections_upd","publicTransportDirections":"somePublicTransportDirections_upd","specialInstructions":"someSpecialInstructions_upd","tags":[],"rooms":[],"documents":[],"rules":[]}

        Given path ishPath + '/' + id
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Invalid longitude value. Longitude must be between 180 and -180"

##       <--->  Update Suburb to >50 symbols:
#        * def siteToUpdate = {"id":"#(id)","isAdministrationCentre":true,"isVirtual":false,"isShownOnWeb":true,"kioskUrl":null,"name":"someSite100_upd","street":"anyStreet_upd","suburb":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A","state":"NSW","postcode":"5001","country":{"id":1,"name":"Australia"},"timezone":"Australia/Sydney","longitude":150.948392,"latitude":-33.822474,"drivingDirections":"someDrivingDirections_upd","publicTransportDirections":"somePublicTransportDirections_upd","specialInstructions":"someSpecialInstructions_upd","tags":[],"rooms":[],"documents":[],"rules":[]}
#
#        Given path ishPath + '/' + id
#        And request siteToUpdate
#        When method PUT
#        Then status 400
#        And match $.errorMessage == "Suburb can't be more than 50 chars."
#
##       <--->  Update State to >50 symbols:
#        * def siteToUpdate = {"id":"#(id)","isAdministrationCentre":true,"isVirtual":false,"isShownOnWeb":true,"kioskUrl":null,"name":"someSite100_upd","street":"anyStreet_upd","suburb":"Holroyd","state":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A","postcode":"5001","country":{"id":1,"name":"Australia"},"timezone":"Australia/Sydney","longitude":150.948392,"latitude":-33.822474,"drivingDirections":"someDrivingDirections_upd","publicTransportDirections":"somePublicTransportDirections_upd","specialInstructions":"someSpecialInstructions_upd","tags":[],"rooms":[],"documents":[],"rules":[]}
#
#        Given path ishPath + '/' + id
#        And request siteToUpdate
#        When method PUT
#        Then status 400
#        And match $.errorMessage == "State can't be more than 50 chars."
#
##       <--->  Update Postcode to >10 symbols:
#        * def siteToUpdate = {"id":"#(id)","isAdministrationCentre":true,"isVirtual":false,"isShownOnWeb":true,"kioskUrl":null,"name":"someSite100_upd","street":"anyStreet_upd","suburb":"Holroyd","state":"NSW","postcode":"12345678901","country":{"id":1,"name":"Australia"},"timezone":"Australia/Sydney","longitude":150.948392,"latitude":-33.822474,"drivingDirections":"someDrivingDirections_upd","publicTransportDirections":"somePublicTransportDirections_upd","specialInstructions":"someSpecialInstructions_upd","tags":[],"rooms":[],"documents":[],"rules":[]}
#
#        Given path ishPath + '/' + id
#        And request siteToUpdate
#        When method PUT
#        Then status 400
#        And match $.errorMessage == "Postcode can't be more than 50 chars."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing site

        * def siteToUpdate =
            """
            {
            "id":99999,
            "isAdministrationCentre":true,
            "isVirtual":false,
            "isShownOnWeb":true,
            "kioskUrl":null,
            "name":"someSite100_upd",
            "street":"anyStreet_upd",
            "suburb":"Adelaide",
            "state":"SA",
            "postcode":"5001",
            "country":{"id":1,"name":"Australia"},
            "timezone":"Australia/South",
            "longitude":137.6007456,
            "latitude":-33.9284989,
            "drivingDirections":"someDrivingDirections_upd",
            "publicTransportDirections":"somePublicTransportDirections_upd",
            "specialInstructions":"someSpecialInstructions_upd",
            "tags":[],
            "rooms":[],
            "documents":[],
            "rules":[]
            }
            """

        Given path ishPath + '/99999'
        And request siteToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Site with id:99999 doesn't exist"