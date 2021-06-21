@ignore
@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/script'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'list/entity/script'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'


        * def newScript = {"enabled":false,"name":"someScriptToUpdate","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}
        * def scriptToUpdate = {"name":"someScriptToUpdate_upd","description":"some description_upd","enabled":true,"trigger":{"type":"Class cancelled","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        
    Scenario: (+) Update existing script by admin

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate')]
        * def id = row.id
#       <--->

        Given path ishPath + '/' + id
        And request scriptToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"someScriptToUpdate_upd",
        "description":"some description_upd",
        "enabled":true,
        "trigger":{"type":"Class cancelled","entityName":null,"cron":null},
        "content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}",
        "lastRun":[],
        "created":"#ignore",
        "modified":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update existing script by notadmin with access rights

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate')]
        * def id = row.id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}


        Given path ishPath + '/' + id
        And request scriptToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"someScriptToUpdate_upd",
        "description":"some description_upd",
        "enabled":true,
        "trigger":{"type":"Class cancelled","entityName":null,"cron":null},
        "content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}",
        "lastRun":[],
        "created":"#ignore",
        "modified":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}
        
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update existing script by notadmin without access rights

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate')]
        * def id = row.id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        * def scriptUpdateTo = {name: 'scriptName11_UPD', enabled: true, content: 'someBody_UPD', trigger: {type: 'On demand'}}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update script. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB
        * configure headers = { Authorization: 'admin'}
        
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Update script to empty

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script body has invalid structure"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty content

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {name: 'scriptName12', enabled: false, content: '', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script body has invalid structure"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->



    Scenario: (-) Update script to empty name

#       <----->  Add a new entity to update and define id:
        Given path ishPath
        And request newScript
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"","description":"some description_upd","enabled":true,"trigger":{"type":"Class cancelled","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script name should be specified"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to existing name

#       <----->  Add a new entities to update and define id:
        * def script1 = {"enabled":false,"name":"someScriptToUpdate1","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script1
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate1')]
        * def id1 = row.id

        * def script2 = {"enabled":false,"name":"someScriptToUpdate2","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script2
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'someScriptToUpdate2')]
        * def id2 = row.id
#       <--->

        * def scriptUpdateTo = {"name":"someScriptToUpdate1","description":"some description_upd","enabled":true,"trigger":{"type":"Class cancelled","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id2
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script name should be unique"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPath + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Update script name to >128 symbols

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName24","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName24')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName_129symbols_4A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128AB","description":"some description_upd","enabled":true,"trigger":{"type":"Class cancelled","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script name's length must be less than 129 characters"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty enabled status

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName15","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName15')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName15","description":"some description_upd","trigger":{"type":"Class cancelled","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Enabled status should be set"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty trigger

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName16","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName16')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName16","description":"some description_upd","enabled":true,"trigger":{},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script trigger should be specified"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty trigger type

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName17","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName17')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName17","description":"some description_upd","enabled":true,"trigger":{"type":"","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script trigger should be specified"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to not existing trigger type

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName37","trigger":{"type":"Enrolment cancelled"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName37')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName37","description":"some description_upd","enabled":true,"trigger":{"type":"not existing","entityName":null,"cron":null},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "Script trigger should be specified"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty trigger cron

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName18","trigger":{"type":"Schedule","cron":{"scheduleType":"Hourly"}},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName18')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName18","description":"some description_upd","enabled":true,"trigger":{"type":"Schedule","cron":{}},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "ScheduleType should be specified for this trigger"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to empty trigger cron scheduleType

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName19","trigger": {"type": "Schedule", "cron": {"scheduleType": "Hourly"}},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName19')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName19","description":"some description_upd","enabled":true,"trigger":{"type":"Schedule","cron":{"scheduleType":""}},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "ScheduleType should be specified for this trigger"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update script to not existing trigger cron scheduleType

#       <----->  Add a new entity to update and define id:
        * def script = {"enabled":false,"name":"scriptName39","trigger":{"type":"Schedule", "cron":{"scheduleType":"Hourly"}},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n\tdef today = new Date()\n// Query closure start \n  def result = query {\n    entity Banking\n    query \"createdOn today \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName39')]
        * def id = row.id
#       <--->

        * def scriptUpdateTo = {"name":"scriptName39","description":"some description_upd","enabled":true,"trigger":{"type":"Schedule","cron": {"scheduleType":"notExisting"}},"content":"import ish.integrations.upd.*\ndef run(args) {\n\tdef endDate = Calendar.getInstance().getTime()\n// Query closure start \n  def result = query {\n    entity Contact\n    query \"createdOn yesterday \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath + '/' + id
        And request scriptUpdateTo
        When method PUT
        Then status 400
        And match response.errorMessage == "ScheduleType should be specified for this trigger"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
