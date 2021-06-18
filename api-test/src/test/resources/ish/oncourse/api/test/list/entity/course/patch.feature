@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/course'

    Background: Authorize first
      * configure headers = { Authorization: 'admin' }
      * url 'https://127.0.0.1:8182/a/v1'
      * def ishPath = 'list/entity/course'
      * def ishPathLogin = 'login'
      * def ishPathList = 'list/plain'
      



    Scenario: (+) Hide/Show Course on website by admin

        Given path ishPath
        And request {"ids":["4","3"],"diff":{"isShownOnWeb":"false"}}
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.status == "Enabled"

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $.status == "Enabled"

#       <--->  Scenario have been finished. Now change back statuses:
        Given path ishPath
        And request {"ids":["4","3"],"diff":{"isShownOnWeb":"true"}}
        When method PATCH
        Then status 204

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.status == "Enabled and visible online"

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $.status == "Enabled and visible online"



    Scenario: (+) Hide/Show Course on website by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        Given path ishPath
        And request {"ids":["4","3"],"diff":{"isShownOnWeb":"false"}}
        When method PATCH
        Then status 204

#       <---> Verification:
        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.status == "Enabled"

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $.status == "Enabled"

#       <--->  Scenario have been finished. Now change back statuses:
        Given path ishPath
        And request {"ids":["4","3"],"diff":{"isShownOnWeb":"true"}}
        When method PATCH
        Then status 204

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $.status == "Enabled and visible online"

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $.status == "Enabled and visible online"



    Scenario: (-) Hide/Show Course on website by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath
        And request {"ids":["4","3"],"diff":{"isShownOnWeb":"false"}}
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit course. Please contact your administrator"