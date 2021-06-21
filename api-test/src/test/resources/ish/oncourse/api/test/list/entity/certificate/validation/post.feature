@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/certificate/validation'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate/validation'
        * def ishPathCertificate = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Verify valid certificate by admin

        Given path ishPath
        And request {"search":"id == 1000","filter":"","tagGroups":[],"sorting":[{"attribute":"awardedOn","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200



    Scenario: (+) Verify revoked certificate by admin

        Given path ishPath
        And request {"search":"id == 1001","filter":"","tagGroups":[],"sorting":[{"attribute":"awardedOn","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200
        And match $ contains "One of selected certificates was revoked. Revoked certificates won't be printed."



    Scenario: (+) Verify revoked certificate by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        Given path ishPath
        And request {"search":"id == 1001","filter":"","tagGroups":[],"sorting":[{"attribute":"awardedOn","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200
        And match $ contains "One of selected certificates was revoked. Revoked certificates won't be printed."



    Scenario: (+) Verify certificate without USI by admin

        Given path ishPath
        And request {"search":"id == 1003","filter":"","tagGroups":[],"sorting":[{"attribute":"awardedOn","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200
        * print response
        And match $ contains 'One of selected users has no USI.'



    Scenario: (+) Verify certificate without USI status by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        Given path ishPath
        And request {"search":"id == 1003","filter":"","tagGroups":[],"sorting":[{"attribute":"awardedOn","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 200
        And match $ contains 'One of selected users has no USI.'



    Scenario: (-) Verify valid certificate by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath
        And request {"search":"id == 1000","filter":"","tagGroups":[],"sorting":[{"attribute":"awardedOn","ascending":true,"complexAttribute":[]}]}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to print certificate. Please contact your administrator"
