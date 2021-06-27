@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/document/export'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/document/export'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Export public document by admin

        Given path ishPath + '/201'
        When method GET
        Then status 200
        And match $ == "defaultPublicDocument"



    Scenario: (+) Export private document by admin

        Given path ishPath + '/200'
        When method GET
        Then status 200
        And match $ == "defaultPrivateDocument"



    Scenario: (+) Export public document by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/201'
        When method GET
        Then status 200
        And match $ == "defaultPublicDocument"

