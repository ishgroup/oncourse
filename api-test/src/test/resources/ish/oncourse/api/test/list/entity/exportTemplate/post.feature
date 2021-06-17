@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/exportTemplate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Create ExportTemplate by admin

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate01",
        "keyCode":"post1",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Text"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post ExportTemplate01"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"post ExportTemplate01",
        "keyCode":"post1",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "outputType":"txt",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create ExportTemplate by notadmin with access rights

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

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate02",
        "keyCode":"post2",
        "entity":"Account",
        "body":"someBody",
        "enabled":false,
        "variables":[{"name":"varName2", "label":"varLabel2", "type":"Date"}],
        "options":[{"name":"optName2","type":"Checkbox","value":true}],
        "outputType":"ics",
        "description":"some description"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post ExportTemplate02"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"post ExportTemplate02",
        "keyCode":"post2",
        "entity":"Account",
        "body":"someBody",
        "enabled":false,
        "variables":[{"name":"varName2","label":"varLabel2","type":"Date","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName2","label":null,"type":"Checkbox","value":"true","system":null,"valueDefault":null}],
        "outputType":"ics",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new ExportTemplate by notadmin without access rights

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

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate03",
        "keyCode":"post3",
        "entity":"Site",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create export template. Please contact your administrator"



    Scenario: (-) Create ExportTemplate with empty Name

        * def newExportTemplate =
        """
        {
        "name":"",
        "keyCode":"post4",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create ExportTemplate with empty keyCode

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate04",
        "keyCode":"",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "keyCode is required."



    Scenario: (-) Create ExportTemplate with empty Entity

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate05",
        "keyCode":"post5",
        "entity":"",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Entity is required."



    Scenario: (-) Create ExportTemplate with empty Body

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate06",
        "keyCode":"post6",
        "entity":"AccountTransaction",
        "body":"",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Body is required."



    Scenario: (-) Create ExportTemplate with empty Output type

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate06b",
        "keyCode":"post6b",
        "entity":"AccountTransaction",
        "body":"some body",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":null
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Output type is required."



    Scenario: (-) Create ExportTemplate with existing keyCode

#       <---> Add entity and define it's id:
        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate07",
        "keyCode":"theSameKeyCode.csv",
        "entity":"Room",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "description":"some description"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post ExportTemplate07"])].id
        * print "id = " + id
 #     <--->

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate08",
        "keyCode":"theSameKeyCode.csv",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "description":"some description"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create ExportTemplate with keyCode starting with ish

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate08",
        "keyCode":"ish.onCourse.account8.csv",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "User template code must not start with ish."



    Scenario: (+) Create ExportTemplate with incorrect 'options' values

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate09",
        "keyCode":"post9",
        "entity":"Account",
        "body":"someBody",
        "enabled":false,
        "variables":[{"name":"varName3", "label":"varLabel3", "type":"Date"}],
        "options":[{"name":"optName3","type":"Date time","value":"2019-01-03"}],
        "outputType":"ics"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "2019-01-03 is not valid for optName3 option"



    Scenario: (-) Create ExportTemplate with Name >100 symbols

        * def newExportTemplate =
        """
        {
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "keyCode":"post10",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 chars."



    Scenario: (-) Create ExportTemplate with keyCode >80 symbols

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate11",
        "keyCode":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "keyCode cannot be more than 80 chars."



    Scenario: (-) Create ExportTemplate with Entity >40 symbols

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate12",
        "keyCode":"post12",
        "entity":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Entity cannot be more than 40 chars."



    Scenario: (-) Create ExportTemplate with not unique Option name

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate13",
        "keyCode":"post13",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName13", "label":"varLabel1", "type":"Text"},{"name":"varName13", "label":"varLabel1", "type":"Text"}],
        "options":[],
        "outputType":"txt"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Option/Variable name should be unique: varname13"



    Scenario: (-) Create ExportTemplate with not unique Variable name

        * def newExportTemplate =
        """
        {
        "name":"post ExportTemplate14",
        "keyCode":"post14",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[{"name":"optName14","type":"Date","value":"2019-01-01"},{"name":"optName14","type":"Date","value":"2019-01-01"}],
        "outputType":"txt"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Option/Variable name should be unique: optname14"



