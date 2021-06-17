@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/room'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/room'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all rooms by admin

        Given path ishPathList
        And param entity = 'Room'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["room1","room2"]



    Scenario: (+) Get list of all rooms by notadmin

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
        And param entity = 'Room'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["room1","room2"]



    Scenario: (+) Get room by admin

        Given path ishPath + "/1"
        When method GET
        Then status 200

        And match $ contains
            """
            {
            "id":1,
            "name":"room1",
            "seatedCapacity":25,
            "siteId":201,
            "kioskUrl":null,
            "directions":null,
            "facilities":null,
            "created":"#ignore",
            "modified":"#ignore",
            "tags":[],
            "documents":[],
            "rules":[],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """



    Scenario: (+) Get room by notadmin

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

        Given path ishPath + "/1"
        When method GET
        Then status 200

        And match $ contains
            """
            {
            "id":1,
            "name":"room1",
            "seatedCapacity":25,
            "siteId":201,
            "kioskUrl":null,
            "directions":null,
            "facilities":null,
            "created":"#ignore",
            "modified":"#ignore",
            "tags":[],
            "documents":[],
            "rules":[],
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }
            """



    Scenario: (-) Get not existing room

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Room with id:99999 doesn't exist"
