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
        