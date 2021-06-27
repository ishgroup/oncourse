@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/script' without license

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathPlain = 'list/plain'
        * def ishPathLogin = 'login'
        


        
    Scenario: (+) Add script without panels by admin

        * def script = {"keyCode":"test.script_1","enabled":true,"name":"script_1","trigger":{"type":"On demand"},"description":"some description","content":"def run(args) {}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param search = 'name == "script_1"'
        And param columns = 'name'
        When method GET
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response contains
        """
        {"id":"#number",
        "name":"script_1",
        "description":"some description",
        "enabled":true,
        "trigger":{"type":"On demand","entityName":null,"entityAttribute":null,"cron":null},"content":"def run(args) {}",
        "lastRun":[],
        "keyCode":test.script_1,
        "content":"def run(args) {}",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add script with Query panel by admin

        * def script = {"keyCode":"test.script_2","name":"script_2","enabled":true,"content":"\n// Query closure start \n  def result = query {\n    entity \"Room\"\n    query \"createdOn is last month \"\n    context args.context\n  }      \n  // Query closure end\n","trigger":{"type":"On create","entityName":"Site","cron":null}, "entity":"Site"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_2"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response contains
        """
        {"id":"#number",
        "name":"script_2",
        "description":null,
        "enabled":true,
        "trigger":{"type":"On create","entityName":"Site","entityAttribute":null,"cron":null},
        "lastRun":[],
        "keyCode":"test.script_2",
        "entity":"Site",
        "content":"#string",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204


#   Scenario: (+) Add script with Email panel by admin
#   Scenario: (+) Add script with Email panel by notadmin with access rights
#   Scenario: (-) Add script with Email panel by notadmin without access rights



    Scenario: (-) Add script with Import panel by admin

        * def script = {"keyCode":"test.script_3","enabled":true,"name":"script_3","trigger":{"type":"On demand"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_3"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add script with Script panel by admin

        * def script = {"keyCode":"test.script_4", "enabled":true,"name":"script_4","trigger":{"type":"On demand"},"description":"some description","content":"def run(args) {\n new Date() \n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_4"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add script without panels by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def script = {"keyCode":"test.script_5","enabled":true,"name":"script_5","trigger":{"type":"On demand"},"description":"some description","content":"def run(args) {}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_5"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response contains
        """
        {"id":"#number",
        "name":"script_5",
        "description":"some description",
        "enabled":true,
        "trigger":{"type":"On demand","entityName":null,"entityAttribute":null,"cron":null},
        "content":"def run(args) {}",
        "lastRun":[],
        "keyCode":"test.script_5",
        "content":"def run(args) {}",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "variables":[],
        "options":[]
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add script with Query panel by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def script = {"keyCode":"test.script_6","name":"script_6","enabled":true,"content":"\n// Query closure start \n  def result = query {\n    entity \"Room\"\n    query \"createdOn is last month \"\n    context args.context\n  }      \n  // Query closure end\n","trigger":{"type":"On create","entityName":"Site"}, "entity":"Site"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_6"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response contains
        """
        {"id":"#number",
        "name":"script_6",
        "description":null,
        "enabled":true,
        "trigger":{"type":"On create","entityName":"Site","entityAttribute":null,"cron":null},
        "lastRun":[],
        "keyCode":"test.script_6",
        "entity":"Site",
        "content":"#string",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "variables":[],
        "options":[]
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization:  'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add script with Query panel by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def script = {"keyCode":"test.script_7","enabled":true,"name":"script_5","trigger":{"type":"On create","entityName":"Site"},"description":"some description","content":"\ndef run(args) {\n\n// Query closure start \n  def result = query {\n    entity Site\n    query \"createdOn last month \"\n    context args.context\n  }      \n  // Query closure end\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create new script. Please contact your administrator"



    Scenario: (-) Add script with Import panel by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def script = {"keyCode":"test.script_8","enabled":true,"name":"script_8","trigger":{"type":"On demand"},"description":"some description","content":"import ish.integrations.*\ndef run(args) {\n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_8"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Add script with Script panel by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        * def script = {"keyCode":"test.script_9","enabled":true,"name":"script_9","trigger":{"type":"On demand"},"description":"some description","content":"def run(args) {\n new Date() \n}"}

        Given path ishPath
        And request script
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Script'
        And param columns = 'name'
        And param search = 'name == "script_9"'
        When method GET
        Then status 200
        * def id = response.rows[0].id

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204