@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/site'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/site'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all sites by admin

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["site1","Default site"]



    Scenario: (+) Get list of all sites by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["site1","Default site"]



    Scenario: (+) Get site by admin

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["site1"]

        * def id = get[0] response.rows[?(@.values == ["[]",null,"site1","Adelaide","5000","false"])].id

        Given path ishPath + "/" + id
        When method GET
        Then status 200

        And match $ contains
            """
            {
            "id":#number,
            "isAdministrationCentre":false,
            "isVirtual":false,
            "isShownOnWeb":false,
            "kioskUrl":null,
            "name":"site1",
            "street":"Frome Rd",
            "suburb":"Adelaide",
            "state":"SA",
            "postcode":"5000",
            "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
            "timezone":"Australia/Sydney",
            "longitude":138.60569150,
            "latitude":-34.91638480,
            "drivingDirections":null,
            "publicTransportDirections":null,
            "specialInstructions":null,
            "tags":[],
            "rooms":
                [
                {"id":1,"name":"room1","customFields":{},"seatedCapacity":25,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null,"virtualRoomUrl":null },
                {"id":2,"name":"room2","customFields":{},"seatedCapacity":50,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null,"virtualRoomUrl":null}
                ],
            "documents":[],
            "rules":[],
            "customFields":{}
            }
            """



    Scenario: (+) Get site by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->
        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["site1"]

        * def id = get[0] response.rows[?(@.values == ["[]",null,"site1","Adelaide","5000","false"])].id

        Given path ishPath + "/" + id
        When method GET
        Then status 200

        And match $ contains
            """
            {
            "id":#number,
            "isAdministrationCentre":false,
            "isVirtual":false,
            "isShownOnWeb":false,
            "kioskUrl":null,
            "name":"site1",
            "street":"Frome Rd",
            "suburb":"Adelaide",
            "state":"SA",
            "postcode":"5000",
            "country":{"id":1,"isoCodeAlpha3":null,"isoCodeNumeric":null,"name":"Australia","saccCode":null},
            "timezone":"Australia/Sydney",
            "longitude":138.60569150,
            "latitude":-34.91638480,
            "drivingDirections":null,
            "publicTransportDirections":null,
            "specialInstructions":null,
            "tags":[],
            "rooms":
                [
                {"id":1,"name":"room1","customFields":{},"seatedCapacity":25,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null,"virtualRoomUrl":null},
                {"id":2,"name":"room2","customFields":{},"seatedCapacity":50,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null,"virtualRoomUrl":null}
                ],
            "documents":[],
            "rules":[],
            "customFields":{}
            }
            """



    Scenario: (-) Get not existing site

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."