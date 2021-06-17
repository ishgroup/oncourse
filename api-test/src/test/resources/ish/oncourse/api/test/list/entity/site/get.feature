@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/site'

    Background: Authorize first
        * call read('../../../signIn.feature')
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
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["site1","Default site"]



    Scenario: (+) Get site by admin

        Given path ishPath + "/201"
        When method GET
        Then status 200

        And match $ ==
            """
            {
            "id":201,
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
                {"id":1,"name":"room1","seatedCapacity":25,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null},
                {"id":2,"name":"room2","seatedCapacity":50,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}
                ],
            "documents":[],
            "rules":[],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """



    Scenario: (+) Get site by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + "/201"
        When method GET
        Then status 200

        And match $ ==
            """
            {
            "id":201,
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
                {"id":1,"name":"room1","seatedCapacity":25,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null},
                {"id":2,"name":"room2","seatedCapacity":50,"siteId":201,"siteTimeZone":null,"kioskUrl":null,"directions":null,"facilities":null,"tags":[],"documents":[],"rules":[],"createdOn":null,"modifiedOn":null}
                ],
            "documents":[],
            "rules":[],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """



    Scenario: (-) Get not existing site

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Site with id:99999 doesn't exist"