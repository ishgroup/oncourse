@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/contact/usi'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/contact/usi'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Verify valid USI by admin

        Given path ishPath
        And request {"firstName":"stud1","lastName":"stud1","birthDate":"2005-05-01","usiCode":"2222222222"}
        When method POST
        Then status 200
        And match $ == {"errorMessage":"Upgrade for automatic verification. Please contact ish support.","verifyStatus":"Disabled"}



    Scenario: (+) Verify valid USI by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And request {"firstName":"stud1","lastName":"stud1","birthDate":"2005-05-01","usiCode":"2222222222"}
        When method POST
        Then status 200
        And match $ == {"errorMessage":"Upgrade for automatic verification. Please contact ish support.","verifyStatus":"Disabled"}



    Scenario: (-) Verify not valid USI

        Given path ishPath
        And request {"firstName":"studentC","lastName":"mergeC","birthDate":"1995-05-06","usiCode":"3333333333"}
        When method POST
        Then status 200
        And match $ == {"errorMessage":"Invalid USI code format.","verifyStatus":"Invalid format"}
