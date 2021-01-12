@parallel=false
Feature: Main feature for all POST requests with path 'user'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'user'
        * def ishPathLogin = 'login'
        * def ishPathPreference = 'preference'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Add new active User: admin

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"12121212","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match each list[*].firstName == 'firstName1'
        * match each list[*].lastName == 'lastName1'
        * match each list[*].email == 'testMail1@gmail.com'
        * match each list[*].admin == true
        * match each list[*].accessEditor == false
        * match each list[*].role == null
        * match each list[*].administrationCentre == 200
        * match each list[*].active == true
        * match each list[*].tfaEnabled == false
        * match each list[*].passwordUpdateRequired == false
        * match each list[*].password == null

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Add new active User: not admin

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName2","lastName":"lastName2","administrationCentre":200,"email":"testMail2@gmail.com","admin":false,"role":200,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match each list[*].firstName == 'firstName2'
        * match each list[*].lastName == 'lastName2'
        * match each list[*].email == 'testMail2@gmail.com'
        * match each list[*].admin == false
        * match each list[*].accessEditor == true
        * match each list[*].role == 200
        * match each list[*].administrationCentre == 200
        * match each list[*].active == true
        * match each list[*].tfaEnabled == false
        * match each list[*].passwordUpdateRequired == false
        * match each list[*].password == null

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Add new inactive User: admin

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":false,"login":"#(newUser)","password":"12121212","firstName":"firstName3","lastName":"lastName3","administrationCentre":200,"email":"testMail3@gmail.com","admin":false,"role":200,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match each list[*].firstName == 'firstName3'
        * match each list[*].lastName == 'lastName3'
        * match each list[*].email == 'testMail3@gmail.com'
        * match each list[*].admin == false
        * match each list[*].accessEditor == true
        * match each list[*].role == 200
        * match each list[*].active == false

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Invalid credentials"
        And match response.errorMessage == "Invalid email / password"
#       <----->



    Scenario: (+) Create new User with valid (5 chars) password

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"12345","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12345', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Create new User with valid (1024 chars) password

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A515A519A523A527A531A535A539A543A547A551A555A559A563A567A571A575A579A583A587A591A595A599A603A607A611A615A619A623A627A631A635A639A643A647A651A655A659A663A667A671A675A679A683A687A691A695A699A703A707A711A715A719A723A727A731A735A739A743A747A751A755A759A763A767A771A775A779A783A787A791A795A799A803A807A811A815A819A823A827A831A835A839A843A847A851A855A859A863A867A871A875A879A883A887A891A895A899A903A907A911A915A919A923A927A931A935A939A943A947A951A955A959A963A967A971A975A979A983A987A991A995A999A1004A1009A1014A1019A1024A","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: 'A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A99A103A107A111A115A119A123A127A131A135A139A143A147A151A155A159A163A167A171A175A179A183A187A191A195A199A203A207A211A215A219A223A227A231A235A239A243A247A251A255A259A263A267A271A275A279A283A287A291A295A299A303A307A311A315A319A323A327A331A335A339A343A347A351A355A359A363A367A371A375A379A383A387A391A395A399A403A407A411A415A419A423A427A431A435A439A443A447A451A455A459A463A467A471A475A479A483A487A491A495A499A503A507A511A515A519A523A527A531A535A539A543A547A551A555A559A563A567A571A575A579A583A587A591A595A599A603A607A611A615A619A623A627A631A635A639A643A647A651A655A659A663A667A671A675A679A683A687A691A695A699A703A707A711A715A719A723A727A731A735A739A743A747A751A755A759A763A767A771A775A779A783A787A791A795A799A803A807A811A815A819A823A827A831A835A839A843A847A851A855A859A863A867A871A875A879A883A887A891A895A899A903A907A911A915A919A923A927A931A935A939A943A947A951A955A959A963A967A971A975A979A983A987A991A995A999A1004A1009A1014A1019A1024A', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Create new User with valid (non-latin chars) password

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"фбдčéž","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: 'фбдčéž', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Create new User with valid (special chars) password

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"§!@#$%^&*()_+=-[];:|><?","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '§!@#$%^&*()_+=-[];:|><?', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (+) Create new User with valid complex password

#       <----->  Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"Aa!@#123","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

#       <-----> Scenario have been finished. Check Log in and change value to default:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: 'Aa!@#123', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->



    Scenario: (+) Add new active User with "passwordUpdateRequired":true

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"12121212","passwordUpdateRequired":true }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match each list[*].passwordUpdateRequired == true

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Forced password update"
#       <----->



    Scenario: (-) Create new User with not unique 'login'

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"admin","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Login should be unique."



    Scenario: (-) Create new invalid (empty) User

        * def someUser = {}

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Login should be set."



    Scenario: (-) Create new User with empty login

        * def someUser = { "active":true,"login":"","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Login should be set."



    Scenario: (-) Create new User with empty password

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Password should be set for new user."



    Scenario: (-) Create new User with not valid (4 chars) password

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"1234","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "You must enter a password that is at least 5 characters long."



    Scenario: (-) Create new User with not valid complex password

#       <----->  Enable "Require better password"
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'true' }]
        When method POST
        Then status 204
#       <----->

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"accessEditor":false,"tfaEnabled":false,"password":"Aa!@#1","passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Password does not satisfy complexity restrictions."

#       <-----> Scenario have been finished. Check Log in and change value to default:
        Given path ishPathPreference
        And request [{ uniqueKey: 'security.password.complexity', valueString: 'false' }]
        When method POST
        Then status 204
#       <----->



    Scenario: (-) Create new User with empty firstName

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "First name should be set."



    Scenario: (-) Create new User with empty lastName

        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "Last name should be set."



#    Scenario: (-) Create new User with empty email
#
#        * def timeStamp = Date.now()
#        * def newUser = 'user_' + timeStamp
#
#        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }
#
#        Given path ishPath
#        And request someUser
#        When method POST
#        Then status 400
#        And match response.errorMessage == "Email should be set for new user."



    Scenario: (-) Create new User with login > 20 symbols

        * def someUser = { "active":true,"login":"User_1234567890123456","password":"password","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 20."



    Scenario: (-) Create new User with firstName > 100 symbols

        * def someUser = { "active":true,"login":"User_123","password":"password","firstName":"*3*5*7*9*12*15*18*21*24*27*30*33*36*39*42*45*48*51*54*57*60*63*66*69*72*75*78*81*84*87*90*93*96*100*1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 100."



    Scenario: (-) Create new User with lastName > 100 symbols

        * def someUser = { "active":true,"login":"User_1234","password":"password","firstName":"firstName1","lastName":"*3*5*7*9*12*15*18*21*24*27*30*33*36*39*42*45*48*51*54*57*60*63*66*69*72*75*78*81*84*87*90*93*96*100*1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 100."



    Scenario: (-) Create new User with email > 128 symbols

        * def someUser = { "active":true,"login":"User_12345","password":"password","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"LoremipsumdolorsitametconsecteturadipiscingelitVestibulumnullalacusgravidavitaemalesuadaegetfeugiatidenimDonecRdghjshhh@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 128."



    Scenario: (+) Update existing User notadmin

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp
        * def updUser = newUser + 'a'

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName4","lastName":"lastName4","administrationCentre":200,"email":"testMail4@gmail.com","admin":false,"role":200,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = {"id":"#(id)","active":false,"login":"#(updUser)","firstName":"firstNameUPD","lastName":"lastNameUPD","administrationCentre":200,"email":"testMailUPD@gmail.com","admin":false,"role":203,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + updUser + "')]")
        * match each list[*].firstName == 'firstNameUPD'
        * match each list[*].lastName == 'lastNameUPD'
        * match each list[*].email == 'testMailUPD@gmail.com'
        * match each list[*].admin == false
        * match each list[*].accessEditor == false
        * match each list[*].role == 203
        * match each list[*].active == false



    Scenario: (+) Update existing User from 'admin' to 'not admin' and then back to 'admin'

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUserToNotAdmin = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":false,"role":200,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserToNotAdmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match list[0].admin == false

        * def updUserToAdmin = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserToAdmin
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match list[0].admin == true



    Scenario: (+) Update existing User from 'active' to 'not active' and then back to 'active'

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUserToInactive = { "id":"#(id)","active":false,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserToInactive
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match list[0].active == false

        * def updUserToActive = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserToActive
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match list[0].active == true



    Scenario: (+) Update role for notadmin

#       <--->  Prepare new User notadmin to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName2","lastName":"lastName2","administrationCentre":200,"email":"testMail2@gmail.com","admin":false,"role":200,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUserRole = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName2","lastName":"lastName2","administrationCentre":200,"email":"testMail2@gmail.com","admin":false,"role":201,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserRole
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match list[0].role == 201



    Scenario: (+) Update "passwordUpdateRequired" to true

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":true }

        Given path ishPath
        And request updUser
        When method POST
        Then status 204

        Given path ishPath
        When method GET
        Then status 200
        And match response[*].login contains "#(newUser)"

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * match each list[*].passwordUpdateRequired == true

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 401
        And match response.loginStatus == "Forced password update"
#       <----->



    Scenario: (-) Update existing User from 'admin' to 'not admin' without role

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUserToNotAdmin = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":false,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUserToNotAdmin
        When method POST
        Then status 400
        And match response.errorMessage == "Not admin users can not be without role."



    Scenario: (-) Update User login to other existing one

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"notadmin","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":false,"accessEditor":true,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "Login should be unique."



    Scenario: (-) Update User login to empty value

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "Login should be set."



    Scenario: (-) Update User password to empty value

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","password":"","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 204

#       <----->  Check Log in
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: "#(newUser)", password: '12121212', kickOut: 'true', skipTfa: 'true'}
        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <----->



    Scenario: (-) Update User password to another value

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","password":"abababab","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400



    Scenario: (-) Update User firstName to empty value

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "First name should be set."



    Scenario: (-) Update User lastName to empty value

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "Last name should be set."



    Scenario: (-) Update User login to > 20 symbols

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"user_1234567890123456","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 20."



    Scenario: (-) Update User firstName to > 100 symbols

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"*3*5*7*9*12*15*18*21*24*27*30*33*36*39*42*45*48*51*54*57*60*63*66*69*72*75*78*81*84*87*90*93*96*100*1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 100."



    Scenario: (-) Update User lastName to > 100 symbols

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"*3*5*7*9*12*15*18*21*24*27*30*33*36*39*42*45*48*51*54*57*60*63*66*69*72*75*78*81*84*87*90*93*96*100*1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 100."



    Scenario: (-) Update email to > 128 symbols

#       <--->  Prepare new User to update it
        * def timeStamp = Date.now()
        * def newUser = 'user_' + timeStamp

        * def someUser = { "active":true,"login":"#(newUser)","password":"12121212","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request someUser
        When method POST
        Then status 204
#       <--->

        Given path ishPath
        When method GET
        Then status 200

        * def list = karate.jsonPath(response, "[?(@.login=='" + newUser + "')]")
        * def id = list[0].id

        * def updUser = { "id":"#(id)","active":true,"login":"#(newUser)","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"LoremipsumdolorsitametconsecteturadipiscingelitVestibulumnullalacusgravidavitaemalesuadaegetfeugiatidenimDonecRdghjshhh@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request updUser
        When method POST
        Then status 400
        And match response.errorMessage == "The maximum length is 128."



    Scenario: (-) Update not existing User

        * def nonExistingUserToUpdate = { "id":99999,"active":true,"login":"user_99999","firstName":"firstName1","lastName":"lastName1","administrationCentre":200,"email":"testMail1@gmail.com","admin":true,"role":null,"accessEditor":false,"tfaEnabled":false,"passwordUpdateRequired":false }

        Given path ishPath
        And request nonExistingUserToUpdate
        When method POST
        Then status 400
        And match response.errorMessage == "User doesn't exist."


