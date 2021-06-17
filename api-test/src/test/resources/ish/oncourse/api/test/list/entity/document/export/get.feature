@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/document/export'

    Background: Authorize first
        * call read('../../../../signIn.feature')
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
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/201'
        When method GET
        Then status 200
        And match $ == "defaultPublicDocument"



#    Scenario: (-) Export private document by notadmin
#
##       <--->  Login as notadmin:
#        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}
#
#        Given path '/login'
#        And request loginBody
#        When method PUT
#        Then status 200
##       <--->
#
#        Given path ishPath + '/200'
#        When method GET
#        Then status 403
#        And match $ == "Sorry, you have no permissions to get export result. Please contact your administrator"

