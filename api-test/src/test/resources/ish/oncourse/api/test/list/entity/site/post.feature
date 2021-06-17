@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/site'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/site'
        * def ishPathList = 'list'
        



    Scenario: (+) Create Site by admin

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":false,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite0",
        "street":"Frome Rd",
        "suburb":"Adelaide",
        "state":"SA",
        "postcode":"5000",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "timezone":"Australia/Sydney",
        "longitude":138.60569150,
        "latitude":-34.91638480,
        "drivingDirections":"someDrivingDirections",
        "publicTransportDirections":"somePublicTransportDirections",
        "specialInstructions":"someSpecialInstructions",
        "tags":[],
        "rooms":[{"siteId":null,"name":"room001","seatedCapacity":"100"}],
        "documents":[],
        "rules":[{"id":null,"description":"www","repeatEnd":"onDate","repeat":"year","repeatEndAfter":0,"repeatOn":"2024-02-12","startDateTime":"2019-12-01T09:07:00.000Z","endDateTime":"2025-12-31T09:07:00.000Z"}]
        }
        """

        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someSite0"]

        * def id = get[0] response.rows[?(@.values == ["someSite0","Adelaide","5000","true"])].id

#       <---> Assertion:
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
        "name":"someSite0",
        "street":"Frome Rd",
        "suburb":"Adelaide",
        "state":"SA",
        "postcode":"5000",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "timezone":"Australia/Sydney",
        "longitude":138.60569150,
        "latitude":-34.91638480,
        "drivingDirections":"someDrivingDirections",
        "publicTransportDirections":"somePublicTransportDirections",
        "specialInstructions":"someSpecialInstructions",
        "tags":[],
        "rooms":
            [
            {"id":"#number","name":"room001","seatedCapacity":100,"siteId":"#number","siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null},
            ],
        "documents":[],
        "rules":[{"id":"#ignore","description":"www","startDate":null,"endDate":null,"startDateTime":"2019-12-01T09:07:00.000Z","endDateTime":"2025-12-31T09:07:00.000Z","repeat":"year","repeatEnd":"onDate","repeatEndAfter":null,"repeatOn":"2024-02-12","created":"#ignore","modified":"#ignore"}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Site with max allowed fields length

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
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

        Given path ishPath
        And request newSite
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["testSite_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150","A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51","1234567890","true"]

        * def id = get[0] response.rows[?(@.values == ["testSite_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150","A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51","1234567890","true"])].id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ contains
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"testSite_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150",
        "street":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
        "suburb":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51",
        "state":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51",
        "postcode":"1234567890",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "timezone":"Australia/Sydney",
        "longitude":180.00000000,
        "latitude":-90.00000000,
        "drivingDirections":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
        "publicTransportDirections":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
        "specialInstructions":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A204A208A212A216A220A224A228A232A236A240A244A248A252A256A",
        "tags":[],
        "rooms":[{"documents":[],"rules":[],"siteTimeZone":null,"createdOn":null,"tags":[],"modifiedOn":null,"directions":null,"seatedCapacity":0,"name":"testSite_2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A150","siteId":"#ignore","kioskUrl":null,"id":"#ignore","facilities":null}],
        "documents":[],
        "rules":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
        

    Scenario: (-) Create Site by notadmin without access rights

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

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite3",
        "street":"Frome Rd",
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

        Given path ishPath
        And request newSite
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create site. Please contact your administrator"



    Scenario: (-) Create Site with empty Name

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"",
        "street":"Frome Rd",
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

        Given path ishPath
        And request newSite
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create Site with existing Name

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"site1",
        "street":"Frome Rd",
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

        Given path ishPath
        And request newSite
        When method POST
        Then status 400
        And match $.errorMessage == "The name of the site must be unique."



    Scenario: (-) Create Site with Name >150 symbols

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"testSite_A2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A151",
        "street":"Frome Rd",
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

        Given path ishPath
        And request newSite
        When method POST
        Then status 400
        And match $.errorMessage == "Name can't be more than 150 chars."



    Scenario: (-) Create Site with Latitude >90 degree

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite6",
        "street":"Frome Rd",
        "suburb":"Adelaide",
        "state":"SA",
        "postcode":"5000",
        "country":{"id":1,"name":"Australia"},
        "timezone":"Australia/Sydney",
        "longitude":138.60569150,
        "latitude":90.00000080,
        "drivingDirections":"someDrivingDirections",
        "publicTransportDirections":"somePublicTransportDirections",
        "specialInstructions":"someSpecialInstructions",
        "tags":[],
        "rooms":[],
        "documents":[],
        "rules":[]
        }
        """

        Given path ishPath
        And request newSite
        When method POST
        Then status 400
        And match $.errorMessage == "Invalid latitude value. Latitude must be between 90 and -90"



    Scenario: (-) Create Site with Longitude >180 degree

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite7",
        "street":"Frome Rd",
        "suburb":"Adelaide",
        "state":"SA",
        "postcode":"5000",
        "country":{"id":1,"name":"Australia"},
        "timezone":"Australia/Sydney",
        "longitude":180.00000150,
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

        Given path ishPath
        And request newSite
        When method POST
        Then status 400
        And match $.errorMessage == "Invalid longitude value. Longitude must be between 180 and -180"



    Scenario: (-) Create Site with empty timezone

        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":false,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite444",
        "street":"Frome Rd",
        "suburb":"Adelaide",
        "state":"SA",
        "postcode":"5000",
        "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
        "timezone":null,
        "longitude":138.60569150,
        "latitude":-34.91638480,
        "drivingDirections":"someDrivingDirections",
        "publicTransportDirections":"somePublicTransportDirections",
        "specialInstructions":"someSpecialInstructions",
        "tags":[],
        "rooms":[{"siteId":null,"name":"room001","seatedCapacity":"100"}],
        "documents":[],
        "rules":[]
        }
        """

        Given path ishPath
        And request newSite
        When method POST
        Then status 400
        And match $.errorMessage == "Timezone is required."




#    Scenario: (-) Create Site with Suburb >50 symbols
#
#        * def newSite =
#        """
#        {
#        "isAdministrationCentre":true,
#        "isVirtual":true,
#        "isShownOnWeb":true,
#        "kioskUrl":null,
#        "name":"someSite4",
#        "street":"Frome Rd",
#        "suburb":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A",
#        "state":"SA",
#        "postcode":"5000",
#        "country":{"id":1,"name":"Australia"},
#        "timezone":"Australia/Sydney",
#        "longitude":138.60569150,
#        "latitude":-34.91638480,
#        "drivingDirections":"someDrivingDirections",
#        "publicTransportDirections":"somePublicTransportDirections",
#        "specialInstructions":"someSpecialInstructions",
#        "tags":[],
#        "rooms":[],
#        "documents":[],
#        "rules":[]
#        }
#        """
#
#        Given path ishPath
#        And request newSite
#        When method POST
#        Then status 400
#        And match $.errorMessage == ""
#
#
#
#    Scenario: (-) Create Site with Postcode >10 symbols
#
#        * def newSite =
#        """
#        {
#        "isAdministrationCentre":true,
#        "isVirtual":true,
#        "isShownOnWeb":true,
#        "kioskUrl":null,
#        "name":"someSite5",
#        "street":"Frome Rd",
#        "suburb":"Adelaide",
#        "state":"SA",
#        "postcode":"12345678901",
#        "country":{"id":1,"name":"Australia"},
#        "timezone":"Australia/Sydney",
#        "longitude":138.60569150,
#        "latitude":-34.91638480,
#        "drivingDirections":"someDrivingDirections",
#        "publicTransportDirections":"somePublicTransportDirections",
#        "specialInstructions":"someSpecialInstructions",
#        "tags":[],
#        "rooms":[],
#        "documents":[],
#        "rules":[]
#        }
#        """
#
#        Given path ishPath
#        And request newSite
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Name is required."
#
#
#
#    Scenario: (-) Create Site with not non-existent Country
#
#        * def newSite =
#        """
#        {
#        "isAdministrationCentre":true,
#        "isVirtual":true,
#        "isShownOnWeb":true,
#        "kioskUrl":null,
#        "name":"someSite6",
#        "street":"Frome Rd",
#        "suburb":"Adelaide",
#        "state":"SA",
#        "postcode":"5000",
#        "country":999,
#        "timezone":"Australia/Sydney",
#        "longitude":138.60569150,
#        "latitude":-34.91638480,
#        "drivingDirections":"someDrivingDirections",
#        "publicTransportDirections":"somePublicTransportDirections",
#        "specialInstructions":"someSpecialInstructions",
#        "tags":[],
#        "rooms":[],
#        "documents":[],
#        "rules":[]
#        }
#        """
#
#        Given path ishPath
#        And request newSite
#        When method POST
#        Then status 400
#        And match $.errorMessage == ""





