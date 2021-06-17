@parallel=false
Feature: Main feature for all PUT requests with path 'user/updatePassword'

    Background: Authorize first
        * call read('../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/updatePassword'
        * def ishPathLogin = 'login'
        * def ishPathPreference = 'preference'


    Scenario: (+) Change password by admin to new valid (5 chars) value

#       Change password to new value
        Given path ishPath + '/' + '123aB'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: '123aB', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Change password by notadmin to new valid (5 chars) value

#       Change password to new value
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + '123aB'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: '123aB', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Change password to new valid long (200 chars) value

        Given path ishPath + '/' + 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A2'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A2', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Change password to new valid value using non-latin chars

        Given path ishPath + '/' + '%D1%84%D0%B1%D0%B4%C4%8D%C3%A9%C5%BE'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'фбдčéž', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Change password to new valid value using special chars

        Given path ishPath + '/' + '%C2%A7!%40%23%24%25%5E%26*()_%2B%3D-%5B%5D%3B%3A%7C%3E%3C%3F'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: '§!@#$%^&*()_+=-[];:|><?', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Change password to the same (old) value

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Complexity: Change password by admin to new valid value

#       <-----> Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

        Given path ishPath + '/' + 'fgH76kju76'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'fgH76kju76', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Complexity: Change password by notadmin to new valid value

#       <-----> Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Weak password"
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: 'password', newPassword: "fgH76kju76ww", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'fgH76kju76'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Weak password"
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', newPassword: "fgH76kju76", kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204

        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'notadmin', password: 'fgH76kju76', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Complexity: Change password to new valid long (200 chars) value

#       <-----> Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

        Given path ishPath + '/' + 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A2'
        And request {}
        When method PUT
        Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A2', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Complexity: Change password to new valid value using non-latin chars

#       <-----> Enable "Require better password"
         Given path ishPathPreference
         And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
         When method POST
         Then status 204
#       <----->

         Given path ishPath + '/' + '%D1%84%D0%B1%D0%B4%C4%8D%C3%A9%C5%BE%D0%A0%D1%8B%D1%83%D1%8E%D1%84'
         And request {}
         When method PUT
         Then status 204

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
         Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'фбдčéžРыуюф', kickOut: 'true', skipTfa: 'true'}
         Given path ishPathLogin
         And request loginBody
         When method PUT
         Then status 200
         And match response.loginStatus == "Login successful"

         Given path ishPathPreference
         And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
         When method POST
         Then status 204

         Given path ishPath + '/' + 'password'
         And request {}
         When method PUT
         Then status 204
#       <----->


    Scenario: (+) Complexity: Change password to new valid value using special chars

 #       <-----> Enable "Require better password"
         Given path ishPathPreference
         And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
         When method POST
         Then status 204
 #       <----->

         Given path ishPath + '/' + '%C2%A7!%40%23%24%25%5E%26*()_%2B%3D-%5B%5D%3B%3A%7C%3E%3C%3F'
         And request {}
         When method PUT
         Then status 204

 #       <-----> Scenario have been finished. Now verificate new password and then change to default value:
         Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: '§!@#$%^&*()_+=-[];:|><?', kickOut: 'true', skipTfa: 'true'}
         Given path ishPathLogin
         And request loginBody
         When method PUT
         Then status 200
         And match response.loginStatus == "Login successful"

         Given path ishPathPreference
         And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
         When method POST
         Then status 204

         Given path ishPath + '/' + 'password'
         And request {}
         When method PUT
         Then status 204
 #       <----->


    Scenario: (+) Change password to valid value after enabling "Require password change every '-1' days"

#       <-----> Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.expiry.period', valueString: "-1"}]
        When method POST
        Then status 204
#       <----->

        Given path '/logout'
        And request {}
        When method PUT
        
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Password outdated"
        And match response.errorMessage == "Password outdated. Update required."

        * def newPass = {login: 'admin', password: 'password', newPassword: 'passwordNew', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request newPass
        When method PUT
        Then status 200

#       <-----> Scenario have been finished. Change password to default value:
        Given path ishPath + '/' + 'password'
        And request {}
        When method PUT
        Then status 204

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.expiry.period', valueString: null }]
        When method POST
        Then status 204
#       <----->


    Scenario: (-) Change password to new valid value using only body

        Given path ishPath
        And request { value: "345tEst" }
        When method PUT
        Then status 404


    Scenario: (-) Change password to empty value

        Given path ishPath + '/' + ''
        And request {}
        When method PUT
        Then status 404


    Scenario: (-) Change password to not valid (<5 chars) value

        Given path ishPath + '/' + '1234'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "You must enter a password that is at least 5 characters long."


    Scenario: (-) Change password to the same value as for login

        Given path ishPath + '/' + 'admin'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "You must enter password which is different to login."


    Scenario: (-) Complexity: Change password to short NOT valid value

#       <-----> Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

        Given path ishPath + '/' + '123456'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        Given path ishPath + '/' + 'aaaaaa'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        Given path ishPath + '/' + '123aaa'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        Given path ishPath + '/' + '12121212'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        Given path ishPath + '/' + 'asasasas'
        And request {}
        When method PUT
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

#       <-----> Scenario have been finished. Now verificate new password and then change to default value:
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->



