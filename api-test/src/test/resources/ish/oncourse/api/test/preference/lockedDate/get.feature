@parallel=false
Feature: Main feature for all GET requests with path 'preference/lockedDate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/lockedDate'




    Scenario: (+) Get lockedDate by admin

        Given path ishPath
        When method GET
        Then status 200
        And match $ == {"lockedDate":"2015-12-31"}



    Scenario: (+) Get lockedDate by notadmin with access rights

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        Given path ishPath
        When method GET
        Then status 200
        And match $ == {"lockedDate":"2015-12-31"}



    Scenario: (+) Get lockedDate by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}


#       <--->

        Given path ishPath
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit banking. Please contact your administrator"
