@parallel=false
Feature: Main feature for all PUT requests with path 'user/resetPassword'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'
        * def ishPath = 'user/resetPassword'
        * def ishPathUser = 'user'
        * def ishPathLogin = 'login'



    Scenario: (+) Reset password for admin

#       <----->  Create a new user to reset password
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"12121212","passwordUpdateRequired":false }

        Given path ishPathUser
        And request someUser
        When method POST
        Then status 204

        Given path ishPathUser
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"
#       <----->

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        Given path ishPath + '/' + id
        And request {}
        When method PUT
        Then status 200

        * def newPassword = response

#       <----->  Check old/new passwords
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Invalid credentials"
        And match response.errorMessage == "User or password incorrect."

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: "#(newPassword)", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Reset password for notadmin

#       <----->  Create a new user to reset password
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName2","lastName":"lastName2","administrationCentre":200,"email":"testMail2@gmail.com","admin":false,"role":200,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPathUser
        And request someUser
        When method POST
        Then status 204

        Given path ishPathUser
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"
#       <----->

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        Given path ishPath + '/' + id
        And request {}
        When method PUT
        Then status 200

        * def newPassword = response

#       <----->  Check old/new passwords
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Invalid credentials"
        And match response.errorMessage == "User or password incorrect."

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: "#(newPassword)", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (-) Reset password by notadmin

#       <----->  Create a new user to reset password
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"12121212","passwordUpdateRequired":false }

        Given path ishPathUser
        And request someUser
        When method POST
        Then status 204

        Given path ishPathUser
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"
#       <----->

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "notadmin", password: "password", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        And request {}
        When method PUT
        Then status 403



    Scenario: (-) Reset password for non-existing user

        Given path ishPath + '/99999'
        And request {}
        When method PUT
        Then status 400
        And match $.errorMessage == "SystemUser with id:99999 doesn't exist"



