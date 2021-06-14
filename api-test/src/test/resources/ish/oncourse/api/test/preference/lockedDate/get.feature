@parallel=false
Feature: Main feature for all GET requests with path 'preference/lockedDate'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'preference/lockedDate'
        



    Scenario: (+) Get lockedDate by admin

        Given path ishPath
        When method GET
        Then status 200
        And match $ == {"year":2015,"month":"DECEMBER","monthValue":12,"dayOfMonth":31,"chronology":{"id":"ISO","calendarType":"iso8601"},"dayOfWeek":"THURSDAY","era":"CE","dayOfYear":365,"leapYear":false}



    Scenario: (+) Get lockedDate by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match $ == {"year":2015,"month":"DECEMBER","monthValue":12,"dayOfMonth":31,"chronology":{"id":"ISO","calendarType":"iso8601"},"dayOfWeek":"THURSDAY","era":"CE","dayOfYear":365,"leapYear":false}



    Scenario: (+) Get lockedDate by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit banking. Please contact your administrator"
