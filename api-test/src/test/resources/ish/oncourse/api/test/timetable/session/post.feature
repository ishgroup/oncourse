@parallel=false
Feature: Main feature for all POST requests with path 'timetable/session'

    Background: Authorize first
        * callonce read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'timetable/session'
        * def ishPathCalendar = 'timetable/calendar'
        



    Scenario: (+) Search Sessions by admin

        * def searchSession = {"from":"2018-10-31T13:00:00.000Z","to":"2019-12-31T12:59:59.999Z","search":"room.site.id=201"}

        Given path ishPath
        And request searchSession
        When method POST
        Then status 200
        And match $[*].id contains [18,34,38,46,49,13,33]

        * def searchSession = {"from":"2019-10-31T13:00:00.000Z","to":"2019-12-31T12:59:59.999Z","search":"room.id=1"}

        Given path ishPath
        And request searchSession
        When method POST
        Then status 200
        And match $[*].id contains [13,33]



    Scenario: (+) Search Sessions by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * def searchSession = {"from":"2018-10-30T13:00:00.000Z","to":"2019-10-31T12:59:59.999Z","search":"room.site.id=201"}

        Given path ishPath
        And request searchSession
        When method POST
        Then status 200
        And match $[*].id contains [18,34,38,46,49]

        * def searchSession = {"from":"2019-10-31T13:00:00.000Z","to":"2019-12-31T12:59:59.999Z","search":"room.id=1"}

        Given path ishPath
        And request searchSession
        When method POST
        Then status 200
        And match $[*].id contains [13,33]