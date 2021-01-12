@parallel=false
Feature: Main feature for all PUT requests with path 'login'

    Background: Authorize first
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'login'
        * def ishPathPreference = 'preference'
        * def ishPathPass = 'user/updatePassword'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Authorize as admin several times

        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'
        And match response.errorMessage == null

        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'
        And match response.errorMessage == null



    Scenario: (+) Authorize as notadmin several times

        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'



    Scenario: (+) Authorize as 2fa user

        * def loginBody = {login: '2fa', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Token required'
        And match response.errorMessage == 'Auth Token required'



    Scenario: (+) Authorize as admin with Tfa

        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'false'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'TFA optional'



    Scenario: (+) Authorize as notadmin with Tfa

        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'false'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'TFA optional'



#    Scenario: (+) Authorize as notadmin without 2fa when "2FA required for admin users" is enabled
#
##       <-----> Enable "2FA required for admin users"
#        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
#        Given path ishPath
#        And request loginBody
#        When method PUT
#        Then status 200
#
#        Given path ishPathPreference
#        And request [{ uniqueKey: 'security.tfa.status', valueString: 'enabled.admin' }]
#        When method POST
#        Then status 204
##       <----->
#
#        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
#        Given path ishPath
#        And request loginBody
#        When method PUT
#        Then status 200
#        And match response.loginStatus == 'Login successful'
#
##       <-----> Scenario have been finished. Change value to default:
#
#        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
#        Given path ishPath
#        And request loginBody
#        When method PUT
#        Then status 200
#
#        Given path ishPathPreference
#        And request [{ uniqueKey: 'security.tfa.status', valueString: 'disabled' }]
#        When method POST
#        Then status 204
##       <----->



    Scenario: (+) Authorize as 'inactive' user when "Automatically disable inactive accounts" is disabled

#       <-----> Disable "Automatically disable inactive accounts" by admin:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->

        * def loginBody = {login: 'inactive', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

#       <-----> Scenario have been finished. Change value to default:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Disable user account after making over than allowed number of login attempts

        #       <-----> Set allowed number of login attempts by admin:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '1' }]
        When method POST
        Then status 204

        Given path '/logout'
        And request {}
        When method PUT

        #       <-----> Try to login to user with wrong password
        * def loginBody = {login: 'incorrectCred', password: 'abracadabra', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.errorMessage == 'User or password incorrect.'

        #       <-----> Try to login to user again, user isn't active, login is impossible
        * def loginBody = {login: 'incorrectCred', password: 'abracadabra', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.errorMessage == 'Invalid email / password'

        #       <-----> Return value to default
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '5' }]
        When method POST
        Then status 204



    Scenario: (+) Log in as admin when "Require better password" is enabled

#       <----->  Enable "Require better password"
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204

        Given path ishPathPreference
        And param search = 'security.password.complexity'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.password.complexity'
        And match response[0].valueString == 'true'
#       <----->

        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Weak password"
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        * def loginBody = {login: 'admin', password: 'password', newPassword: "Aa!@#123", kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <-----> Scenario have been finished. Change value to default:
        * def loginBody = {login: 'admin', password: 'Aa!@#123', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPathPass + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->


    Scenario: (+) Log in as notadmin when "Require better password" is enabled

#       <----->  Enable "Require better password"
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == 'Login successful'

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204

        Given path ishPathPreference
        And param search = 'security.password.complexity'
        When method GET
        Then status 200
        And match response[0].uniqueKey == 'security.password.complexity'
        And match response[0].valueString == 'true'
#       <----->

        * def loginBody = {login: 'notadmin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Weak password"
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

        * def loginBody = {login: 'notadmin', password: 'password', newPassword: "Aa!@#123", kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

#       <-----> Scenario have been finished. Change value to default:
        * def loginBody = {login: 'admin', password: 'password', newPassword: 'Aa!@#123', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204

        Given path ishPathPass + '/' + 'password'
        And request {}
        When method PUT
        Then status 204

        * def loginBody = {login: 'notadmin', password: 'Aa!@#123', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathPass + '/' + 'password'
        And request {}
        When method PUT
        Then status 204
#       <----->



    Scenario: (-) Send empty request

        * def emptyLoginBody = {}
        Given path ishPath
        And request emptyLoginBody
        When method PUT
        Then status 400
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'Email / password data must be specified'



    Scenario: (-) Send authorize request without 'authorization' data in body

        * def emptyLoginBody = {login: '', password: '', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request emptyLoginBody
        When method PUT
        Then status 400
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'Email / password data must be specified'



    Scenario: (-) Send authorize request with data of unexisting user

        * def wrongLoginBody = {login: 'notExistingLogin', password: 'notExistingPassword', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'



    Scenario: (-) Send authorize request with empty login

        * def wrongLoginBody = {login: '', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 400
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'Email / password data must be specified'



    Scenario: (-) Send authorize request with unexisting login

        * def wrongLoginBody = {login: 'notExistingLogin', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'



    Scenario: (-) Send authorize request with empty password

        * def wrongLoginBody = {login: 'admin', password: '', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 400
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'Email / password data must be specified'



    Scenario: (-) Send authorize request with unexisting password

        * def wrongLoginBody = {login: 'admin', password: 'notExistingPassword', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'User or password incorrect.'



    Scenario: (-) Send authorize request with the same login and password

        * def wrongLoginBody = {login: 'admin', password: 'admin', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'User or password incorrect.'

        * def wrongLoginBody = {login: 'password', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request wrongLoginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'Invalid email / password'



    Scenario: (-) Authorize as 'inactive' user when "Automatically disable inactive accounts" is enabled

        * def loginBody = {login: 'inactive', password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPath
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == 'Invalid credentials'
        And match response.errorMessage == 'Invalid email / password'


#    Scenario: (-) Authorize as admin without 2fa when "2FA required for admin users" is enabled
#
##       <-----> Enable "2FA required for admin users"
#        Given path ishPath
#        And request [{ uniqueKey: 'security.tfa.status', valueString: 'enabled.admin' }]
#        When method POST
#        Then status 204
##       <----->
#
#        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
#        Given path ishPath
#        And request loginBody
#        When method PUT
#        Then status 401
#        And match response.loginStatus == 'Token required'
#        And match response.errorMessage == 'Auth Token required'
#
##       <-----> Scenario have been finished. Change value to default:
#        Given path ishPath
#        And request [{ uniqueKey: 'security.tfa.status', valueString: 'disabled' }]
#        When method POST
#        Then status 204
##       <----->
#
#
#    Scenario: (-) Authorize as admin without 2fa when "2FA required for all users" is enabled
#
##       <-----> Enable "2FA required for admin users"
#        Given path ishPath
#        And request [{ uniqueKey: 'security.tfa.status', valueString: 'enabled.all' }]
#        When method POST
#        Then status 204
##       <----->
#
#        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}
#        Given path ishPath
#        And request loginBody
#        When method PUT
#        Then status 401
#        And match response.loginStatus == 'Token required'
#        And match response.errorMessage == 'Auth Token required'
#
##       <-----> Scenario have been finished. Change value to default:
#        Given path ishPath
#        And request [{ uniqueKey: 'security.tfa.status', valueString: 'disabled' }]
#        When method POST
#        Then status 204
##       <----->


