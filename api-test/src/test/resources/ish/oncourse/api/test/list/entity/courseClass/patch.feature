@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/courseClass'

    Background: Authorize first
      * call read('../../../signIn.feature')
      * url 'https://127.0.0.1:8182/a/v1'
      * def ishPath = 'list/entity/courseClass'
      * def ishPathLogin = 'login'
      * def ishPathList = 'list/plain'
      



    Scenario: (+) Hide/Show Class on website by admin

        Given path ishPath
        And request {"ids":["2","4"],"diff":{"isShownOnWeb":"false"}}
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $.isShownOnWeb == false

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.isShownOnWeb == false

#       <--->  Scenario have been finished. Now change back statuses:
        Given path ishPath
        And request {"ids":["2","4"],"diff":{"isShownOnWeb":"true"}}
        When method PATCH
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $.isShownOnWeb == true

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.isShownOnWeb == true



    Scenario: (+) Hide/Show Class on website by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        And request {"ids":["2","4"],"diff":{"isShownOnWeb":"false"}}
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $.isShownOnWeb == false

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.isShownOnWeb == false

#       <--->  Scenario have been finished. Now change back statuses:
        Given path ishPath
        And request {"ids":["2","4"],"diff":{"isShownOnWeb":"true"}}
        When method PATCH
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $.isShownOnWeb == true

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.isShownOnWeb == true



    Scenario: (-) Hide/Show Class on website by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        And request {"ids":["2","4"],"diff":{"isShownOnWeb":"false"}}
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit class. Please contact your administrator"