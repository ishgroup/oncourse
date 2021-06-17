@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/site'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/site'
        * def ishPathList = 'list'
        


        
    Scenario: (+) Delete existing site

#       <----->  Add a new site for deleting and define id:
        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite10",
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
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someSite10","Adelaide","5000","true"]

        * def id = get[0] response.rows[?(@.values == ["someSite10","Adelaide","5000","true"])].id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["someSite10"]



    Scenario: (+) Delete existing site by notadmin with rights

#       <----->  Add a new site for deleting and define id:
        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite11",
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
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someSite11","Adelaide","5000","true"]

        * def id = get[0] response.rows[?(@.values == ["someSite11","Adelaide","5000","true"])].id

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification deleting:
        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["someSite11"]



    Scenario: (-) Delete existing site by notadmin without rights

#       <----->  Add a new site for deleting and define id:
        * def newSite =
        """
        {
        "isAdministrationCentre":true,
        "isVirtual":true,
        "isShownOnWeb":true,
        "kioskUrl":null,
        "name":"someSite12",
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
        Then status 204

        Given path ishPathList
        And param entity = 'Site'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["someSite12","Adelaide","5000","true"]

        * def id = get[0] response.rows[?(@.values == ["someSite12","Adelaide","5000","true"])].id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete site. Please contact your administrator"

#       <---->  Scenario have been finished. Now change back permissions and delete created entity:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete site when rooms have assigned to waitingList

        Given path ishPath + '/201'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Cannot delete site assigned to waiting lists."



    Scenario: (-) Delete NOT existing site

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Site with id:99999 doesn't exist"
