@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/room'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/room'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Delete existing room

#       <----->  Add a new room for deleting and define id:
        * def newRoom =
        """
        {
        "name":"testRoom10",
        "seatedCapacity":100,
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

        * def id = get[0] response.rows[?(@.values == ["testRoom10","site1","100"])].id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["testRoom10"]



    Scenario: (+) Delete existing room by notadmin with rights

#       <----->  Add a new room for deleting and define id:
        * def newRoom =
        """
        {
        "name":"testRoom11",
        "seatedCapacity":100,
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

        * def id = get[0] response.rows[?(@.values == ["testRoom11","site1","100"])].id

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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["testRoom11"]



    Scenario: (-) Delete existing room by notadmin without rights

#       <----->  Add a new room for deleting and define id:
        * def newRoom =
        """
        {
        "name":"testRoom12",
        "seatedCapacity":100,
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

        * def id = get[0] response.rows[?(@.values == ["testRoom12","site1","100"])].id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete room. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Delete room with assigned sessions

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Cannot delete room assigned to sessions."



    Scenario: (-) Delete NOT existing room

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Room with id:99999 doesn't exist"
