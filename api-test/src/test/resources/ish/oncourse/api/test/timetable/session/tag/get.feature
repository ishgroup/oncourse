@parallel=false
Feature: Main feature for all GET requests with path 'timetable/session/tag'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'timetable/session/tag'
        



    Scenario: (+) Get sessions by admin

        Given path ishPath
        And param sessionIds = "13,33"
        When method GET
        Then status 200
        And match response == "#present"



    Scenario: (+) Get sessions by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param sessionIds = "13,33"
        When method GET
        Then status 200
        And match response == "#present"
