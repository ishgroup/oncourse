@parallel=false
Feature: Main feature for all PUT requests with path 'user/resetPassword'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/resetPassword'
        * def ishPathUser = 'user'
        * def ishPathLogin = 'login'



    Scenario: (+) Reset password for user

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT

        * def loginBody = {login: "toResetPassword@gmail.com", password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Invalid credentials"
        And match response.errorMessage == "Login access was disabled after too many incorrect login attempts. Please contact onCourse Administrator."
#       <----->

#       <----->  Log in by admin
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
#       <----->

        Given path ishPathUser
        When method GET
        Then status 200
        And match response[*].email contains "toResetPassword@gmail.com"

        * def list = karate.jsonPath(response, "[?(@.email=='toResetPassword@gmail.com')]")
        * match list[0].active == true
        * match list[0].inviteAgain == false
        * match list[0].passwordLastChanged != null
        * def id = list[0].id

#       <----->  Reset password
        Given path ishPath + '/' + id
        And request {}
        When method PUT
        Then status 204



    Scenario: (-) Reset password for non-existing user

        Given path ishPath + '/99999'
        And request {}
        When method PUT
        Then status 400
        And match $.errorMessage == "SystemUser with id:99999 doesn't exist"



