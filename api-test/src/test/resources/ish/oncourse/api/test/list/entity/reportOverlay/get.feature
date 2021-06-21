@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/reportOverlay'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/reportOverlay'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get overlay by admin

        Given path ishPath + '/100'
        When method GET
        Then status 200
        And match $ == {"id":100,"name":"Certificate","preview":"#ignore"}



    Scenario: (+) Get overlay by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/101'
        When method GET
        Then status 200
        And match $ == {"id":101,"name":"OROR Training","preview":"#ignore"}


