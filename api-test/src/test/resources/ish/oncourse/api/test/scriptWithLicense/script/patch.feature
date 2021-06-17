@ignore
@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/script'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'list/entity/script'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'



    Scenario: (+) Update script (without panels) by admin

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript1","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript1')]
        * def id = row.id
#       <--->

        * def scriptToPatch = {"name":"testScript1_upd","description":"some description_upd","enabled":false,"trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testScript1_upd",
        "description":"some description_upd",
        "enabled":false,
        "trigger":{"type":"On edit","entityName":"Account","cron":null},
        "content":"\ndef run(args) {\n}",
        "lastRun":[],
        "created":"#ignore",
        "modified":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script (with panels) by admin

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":false,"name":"testScript4","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript4')]
        * def id = row.id
#       <--->

        * def scriptToPatch = {"name":"testScript4_upd","description":"some description_upd","enabled":false,"trigger":{"type":"Class cancelled","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 204

#       <---> Assertion. Panels should not be changed using PATCH request:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testScript4_upd",
        "description":"some description_upd",
        "enabled":false,
        "trigger":{"type":"Class cancelled","entityName":null,"cron":null},
        "content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}",
        "lastRun":[],
        "created":"#ignore",
        "modified":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update script (without panels) by notadmin with access rights

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript2","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript2')]
        * def id = row.id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def scriptToPatch = {"name":"testScript2_upd","description":"some description_upd","enabled":false,"trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"testScript2_upd",
        "description":"some description_upd",
        "enabled":false,
        "trigger":{"type":"On edit","entityName":"Account","cron":null},
        "content":"\ndef run(args) {\n}",
        "lastRun":[],
        "created":"#ignore",
        "modified":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script (all except panels) by notadmin without access rights

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript3","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript3')]
        * def id = row.id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def scriptToPatch = {"name":"testScript3_upd","description":"some description_upd","enabled":false,"trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update script. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script (without panels) to empty name using PATCH request

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript5","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript5')]
        * def id = row.id
#       <--->

        * def scriptToPatch = {"name":"","description":"some description_upd","enabled":false,"trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 400
        And match response.errorMessage == "Script name should be specified"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to existing name using PATCH request

#       <----->  Add a new entities to update and define id:
        * def newScript1 = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript6","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript1
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript6')]
        * def id1 = row.id

        * def newScript2 = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript7","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript2
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript7')]
        * def id2 = row.id
#       <--->

        * def scriptToPatch = {"name":"testScript6","description":"some description_upd","enabled":false,"trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id2
        And request scriptToPatch
        When method PATCH
        Then status 400
        And match response.errorMessage == "Script name should be unique"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPath + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Update script name to >128 symbols using PATCH request

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript8","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript8')]
        * def id = row.id
#       <--->

        * def scriptToPatch = {"name":"scriptName_129symbols_4A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128AB","description":"some description_upd","enabled":false,"trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 400
        And match response.errorMessage == "Script name's length must be less than 129 characters"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty enabled status using PATCH request

#       <----->  Add a new entity to update and define id:
        * def newScript = {"enabled":true,"content":"\ndef run(args) {\n}","name":"testScript9","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description"}

        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'testScript9')]
        * def id = row.id
#       <--->

        * def scriptToPatch = {"name":"testScript6","description":"some description_upd","trigger":{"type":"On edit","entityName":"Account","cron":{"scheduleType":"Hourly","custom":null}},"content":"\ndef run(args) {\n}"}

        Given path ishPath + '/' + id
        And request scriptToPatch
        When method PATCH
        Then status 400
        And match response.errorMessage == "Enabled status should be set"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
