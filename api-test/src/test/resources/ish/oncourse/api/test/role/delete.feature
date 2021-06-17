@parallel=false
Feature: Main feature for all DELETE requests with path 'role'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'role'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Delete existing Role without assigned users

#       <--->  Prepare new Role to delete it:
        * def someRole = { "name":"someUserRole" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'someUserRole')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 10
        And match response[*].name !contains "someUserRole"



    Scenario: (-) Delete existing Role with assigned users

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'Administration Manager')].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 400
        And match response.errorMessage == "User role is assigned for some users."



    Scenario: (-) Delete existing Role by notadmin

#       <--->  Prepare new Role to delete it and difine id:
        * def someRole = { "name":"someUserRole111" }

        Given path ishPath
        And request someRole
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def id = get[0] response[?(@.name == 'someUserRole111')].id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Only users with admin rights can do it. Please contact your administrator"

#       <---> Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete not existing Role
        Given path ishPath + '/100000'
        When method DELETE
        Then status 400
        And match response.errorMessage == "ACLRole with id:100000 doesn't exist"
        