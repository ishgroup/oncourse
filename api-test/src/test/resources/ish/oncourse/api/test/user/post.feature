@parallel=false
Feature: Main feature for all POST requests with path 'user'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'user'
        * def ishPathLogin = 'login'
        * def ishPathInvite = 'invite'
        * def ishPathPreference = 'preference'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Try to create a new user: it's impossible due to there is not able to send an invitation

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204


    Scenario: (+) Search user by exist invitation

        Given path ishPathInvite + "/abracadabra"
        When method GET
        Then status 200
        And match response == "invited@gmail.com"


    Scenario: (-) Search user by not exist invitation

        Given path ishPathInvite + "/notExistAbracadabra"
        When method GET
        Then status 400
        And match response.errorMessage == "User not found"


    Scenario: (-) Search user by expired invitation

        Given path ishPathInvite + "/expiredAbracadabra"
        When method GET
        Then status 400
        And match response.errorMessage == "Sorry, but invitation was expired"


    Scenario: (-) Try to create a new password like email

        Given path ishPathInvite + "/abracadabra"
        And params {"password":"invited@gmail.com"}
        And request {}
        When method POST
        Then status 400
        And match response.errorMessage == "You must enter password which is different to email."


    Scenario: (-) Try to create a new password which is less than 5 characters

        Given path ishPathInvite + "/abracadabra"
        And params {"password":"1234"}
        And request {}
        When method POST
        Then status 400
        And match response.errorMessage == "You must enter a password that is at least 5 characters long."


    Scenario: (-) Try to create a new password which is not complexed

#       <----->  Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

        Given path ishPathInvite + "/abracadabra"
        And params {"password":"easy"}
        And request {}
        When method POST
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

#       <----->  Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->


    Scenario: (+) Create a new password for user who was invited

        Given path ishPathInvite + "/abracadabra"
        And params {"password":"password"}
        And request {}
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].email contains "invited@gmail.com"

        * def list = karate.jsonPath(response, "[?(@.email=='invited@gmail.com')]")
        * match each list[*].firstName == 'invited'
        * match each list[*].lastName == 'user'
        * match each list[*].email == 'invited@gmail.com'
        * match each list[*].admin == true
        * match each list[*].accessEditor == false
        * match each list[*].role == null
        * match each list[*].administrationCentre == 200
        * match each list[*].active == true
        * match each list[*].tfaEnabled == false
        * match each list[*].passwordUpdateRequired == false
        * match each list[*].password == null
        * match each list[*].inviteAgain == false


#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "invited@gmail.com", password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->


    Scenario: (-) Add new active User with "passwordUpdateRequired":true

        * def someUser = {"id":"310","active":true,"firstName":"invited","lastName":"user","administrationCentre":200,"email":"invited@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":true }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].email contains "invited@gmail.com"

        * def list = karate.jsonPath(response, "[?(@.login=='invited@gmail.com')]")
        * match each list[*].passwordUpdateRequired == true

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "invited@gmail.com", password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Forced password update"
#       <----->


    Scenario: (-) Create new User with not unique 'email'

        * def someUser = {"active":true,"firstName":"invited","lastName":"user","administrationCentre":200,"email":"invited@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Email should be unique."


    Scenario: (-) Create new invalid (empty) User

        * def someUser = {}

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Email should be set."


    Scenario: (-) Create new User with empty login

        * def someUser = { "active":true,"firstName":"invited","lastName":"user","administrationCentre":200,"email":"","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Email should be set."


    Scenario: (-) Create new User with empty firstName

        * def someUser = { "active":true,"firstName":"","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "First name should be set."


    Scenario: (-) Create new User with empty lastName

        * def someUser = { "active":true,"firstName":"firstName1","lastName":"","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Last name should be set."


    Scenario: (-) Create new User with login > 128 symbols

        * def someUser = { "active":true,"firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail11testMail11testMail11testMail11testMail11testMail11testMail11testMail11testMail11testMail11testMail11testMail11testMail11@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum email length is 128."


    Scenario: (-) Create new User with firstName > 100 symbols

        * def someUser = { "active":true,"firstName":"*3*5*7*9*12*15*18*21*24*27*30*33*36*39*42*45*48*51*54*57*60*63*66*69*72*75*78*81*84*87*90*93*96*100*1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum first name length is 100."


    Scenario: (-) Create new User with lastName > 100 symbols

        * def someUser = { "active":true,"firstName":"firstName1","lastName":"*3*5*7*9*12*15*18*21*24*27*30*33*36*39*42*45*48*51*54*57*60*63*66*69*72*75*78*81*84*87*90*93*96*100*1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum last name length is 100."


    Scenario: (-) Create new User with password

        * def someUser = {"password":"password","active":true,"firstName":"firstName1","lastName":"lastName","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Password cannot be created for new users."


    Scenario: (-) Create new User with not exist site

        * def someUser = {"active":true,"firstName":"firstName1","lastName":"lastName","administrationCentre":200200200,"email":"testMail2@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Administration centre doesn't exist."


    Scenario: (-) Create new not admin User without role

        * def someUser = {"active":true,"firstName":"firstName1","lastName":"lastName","administrationCentre":200,"email":"testMail2@gmail.com","admin":false,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Not admin users can not be without role."


    Scenario: (-) Create new not admin User with not exist role

        * def someUser = {"active":true,"firstName":"firstName1","lastName":"lastName","administrationCentre":200,"email":"testMail2@gmail.com","admin":false,"role":1111111,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "User role doesn't exist."


    Scenario: (+) Update existing User

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.email=='toUpdateUser@gmail.com')]")
        * def id = list[0].id

        * def updUser = {"id":"#(id)","active":false,"firstName":"updated","lastName":"updUser","administrationCentre":200,"email":"updatedUser@gmail.com","admin":false,"role":203,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.email=='updatedUser@gmail.com')]")
        * match each list[*].firstName == 'updated'
        * match each list[*].lastName == 'updUser'
        * match each list[*].email == 'updatedUser@gmail.com'
        * match each list[*].admin == false
        * match each list[*].accessEditor == false
        * match each list[*].role == 203
        * match each list[*].active == false
        * match each list[*].login == null
        * match each list[*].inviteAgain == false


    Scenario: (+) Try to login with incorrect password, then make user inactive, then make active: number of login attempts should be reset to 0

#       <----->  Set special preferences
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '1' }]
        When method POST
        Then status 204
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "updatedUser@gmail.com", password: 'abracadabra', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Invalid credentials"
        And match response.errorMessage == "User or password incorrect."

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

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.email=='updatedUser@gmail.com')]")
        * def id = list[0].id

        * def updUserToActive = { "id":"#(id)","active":true,"firstName":"updated","lastName":"updUser","administrationCentre":200,"email":"updatedUser@gmail.com","admin":false,"role":203,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserToActive
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = karate.jsonPath(response, "[?(@.email=='updatedUser@gmail.com')]")
        * match list[0].active == true

#       <----->  Rollback values of special preferences
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.number.login.attempts', valueString: '5' }]
        When method POST
        Then status 204
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.auto.disable.inactive.account', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "updatedUser@gmail.com", password: 'password', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"



    Scenario: (-) Update User login to other existing one

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.email=='invited@gmail.com')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"firstName":"invited","lastName":"user","administrationCentre":200,"email":"updatedUser@gmail.com","admin":true,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "Email should be unique."


    Scenario: (-) Update not existing User

        * def nonExistingUserToUpdate = { "id":99999,"active":true,"login":"user_99999","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request nonExistingUserToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "User doesn't exist."


