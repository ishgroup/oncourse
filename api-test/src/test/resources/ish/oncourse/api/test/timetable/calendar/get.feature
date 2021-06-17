@parallel=false
Feature: Main feature for all GET requests with path 'timetable/calendar'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'timetable/calendar'
        



    Scenario: (+) Get calendar by admin

        Given path ishPath + "/2/2019"
        And param search = "room.site.id=201"
        When method GET
        Then status 200
        And match $ == [1]

        Given path ishPath + "/11/2019"
        And param search = "room.id=1"
        When method GET
        Then status 200
        And match $ == [1]



    Scenario: (+) Get calendar by notadmin

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

        Given path ishPath + "/11/2019"
        And param search = "room.site.id=201"
        When method GET
        Then status 200
        And match $ == [1]

        Given path ishPath + "/11/2018"
        And param search = "room.id=1"
        When method GET
        Then status 200
        And match $ == [1]