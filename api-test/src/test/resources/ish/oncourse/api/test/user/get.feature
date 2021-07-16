@parallel=false
Feature: Main feature for all GET requests with path 'user'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'user'
        


    Scenario: (+) Get all users by admin

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains ['admin', 'notadmin', '2fa', 'inactive']


    Scenario: (-) Get all users by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath
        When method GET
        Then status 403







