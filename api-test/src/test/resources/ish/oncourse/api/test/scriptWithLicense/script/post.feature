@ignore
@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/script'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Add new valid script by admin

        * def script = {name: 'scriptName2', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName2')]
        * def id = row.id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response.name == 'scriptName2'
        And match response.enabled == false
        And match response.content == 'def run(args) {\n \n}'
        And match response.trigger.type == 'Schedule'
        And match response.trigger.cron.scheduleType == 'Hourly'

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (+) Add new valid script with Name of 128 symbols

        * def script = {name: 'scriptName_128symbols_4A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 204

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName_128symbols_4A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A')]
        * def id = row.id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (+) Add new valid script by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def script = {name: 'scriptName33', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName33')]
        * def id = row.id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response.name == 'scriptName33'
        And match response.enabled == false
        And match response.content == 'def run(args) {\n \n}'
        And match response.trigger.type == 'Schedule'
        And match response.trigger.cron.scheduleType == 'Hourly'

#       <--->  Scenario have been finished. Now find and remove created object from DB and change permissions back:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add new valid script by notadmin without rights

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

        * def script = {name: 'scriptName34', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create new script. Please contact your administrator"



    Scenario: (-) Add new invalid empty script

        * def script = {}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script body has invalid structure"


    Scenario: (-) Add new invalid script without script body

        * def script = {name: 'scriptName4', enabled: false, content: '', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script body has invalid structure"


    Scenario: (-) Add new invalid script without name

        * def script = {name: '', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script name should be specified"


    Scenario: (-) Add new invalid script with existing name

#       <----->  Add script to create duplicate:
        * def script = {name: 'scriptName5', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 204
#       <----->

        * def script = {name: 'scriptName5', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script name should be unique"

#       <--->  Scenario have been finished. Now find and remove created object from DB
        * print "Scenario have been finished. Now find and remove created object from DB"

        Given path ishPathList
        And param entity = 'Script'
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'scriptName5')]
        * def id = row.id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
#       <--->


    Scenario: (-) Add new invalid script with name length >128 symbols

        * def script = {name: 'scriptName_129symbols_4A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128AB', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script name's length must be less than 129 characters"


    Scenario: (-) Add new invalid script without enabled status

        * def script = {name: 'scriptName6', content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Enabled status should be set"


    Scenario: (-) Add new invalid script without trigger

        * def script = {name: 'scriptName2', enabled: false, content: 'def run(args) {\n \n}', trigger: {}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script trigger should be specified"


    Scenario: (-) Add new invalid script without trigger type

        * def script = {name: 'scriptName2', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: '', cron: {scheduleType: 'Hourly'}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "Script trigger should be specified"


    Scenario: (-) Add new invalid script without trigger cron

        * def script = {name: 'scriptName2', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "ScheduleType should be specified for this trigger"


    Scenario: (-) Add new invalid script without trigger cron scheduleType

        * def script = {name: 'scriptName2', enabled: false, content: 'def run(args) {\n \n}', trigger: {type: 'Schedule', cron: {scheduleType: ''}}}

        Given path ishPath
        And request script
        When method POST
        Then status 400
        And match response.errorMessage == "ScheduleType should be specified for this trigger"