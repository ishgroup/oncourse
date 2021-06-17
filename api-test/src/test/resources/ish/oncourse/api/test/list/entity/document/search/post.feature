@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/document/search'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/document/search'
        * def ishPathDoc = 'list/entity/document'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Check non existent document by admin

        * def someStream = read('../testDoc7.jpg')

        Given path ishPath
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 204



    Scenario: (+) Check non existent document by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def someStream = read('../testDoc7.jpg')

        Given path ishPath
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 204



    Scenario: (+) Check existent document by admin

#       <---> Check existent private document:
        * def someStream = read('../defaultPrivateDocument.txt')

        Given path ishPath
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200
        And match $.name == "defaultPrivateDocument"

#       <---> Check existent public document:
        * def someStream = read('../defaultPublicDocument.txt')

        Given path ishPath
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200
        And match $.name == "defaultPublicDocument"



    Scenario: (+) Check existent document by notadmin with rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

#       <---> Check existent private document:
        * def someStream = read('../defaultPrivateDocument.txt')

        Given path ishPath
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200
        And match $.name == "defaultPrivateDocument"

#       <---> Check existent public document:
        * def someStream = read('../defaultPublicDocument.txt')

        Given path ishPath
        And header Content-Type = 'application/octet-stream'
        And request someStream
        When method POST
        Then status 200
        And match $.name == "defaultPublicDocument"



#    Scenario: (-) Check existent private document by notadmin without rights
#
##       <--->  Login as notadmin:
#        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}
#
#        Given path '/login'
#        And request loginBody
#        When method PUT
#        Then status 200
#
##       <---> Check existent private document:
#        * def someStream = read('../defaultPrivateDocument.txt')
#
#        Given path ishPath
#        And header Content-Type = 'application/octet-stream'
#        And request someStream
#        When method POST
#        Then status 403
#        And match $.errorMessage == "Sorry, you have no permissions to get documents. Please contact your administrator"
