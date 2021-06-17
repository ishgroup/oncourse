@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/report'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/report'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Create Report by admin

        * def newReport =
        """
        {
        "name":"post Report01",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"post1",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Text"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"},{"name":"optName2","type":"Text","value":"someValue"}]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['post Report01'])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "name":"post Report01",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"post1",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":"#present",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

        And match $.options contains {"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}
        And match $.options contains {"name":"optName2","label":null,"type":"Text","value":"someValue","system":null,"valueDefault":null}

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Report by notadmin with access rights

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

        * def newReport =
        """
        {
        "name":"post Report02",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post2",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName2", "label":"varLabel2", "type":"Date"}],
        "options":[{"name":"optName2","type":"Checkbox","value":true}],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['post Report02'])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "name":"post Report02",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post2",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName2","label":"varLabel2","type":"Date","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName2","label":null,"type":"Checkbox","value":"true","system":null,"valueDefault":null}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new Report by notadmin without access rights

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

       * def newReport =
        """
        {
        "name":"post Report03",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post3",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName2", "label":"varLabel2", "type":"Date"}],
        "options":[{"name":"optName2","type":"Checkbox","value":true}],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create report. Please contact your administrator"



    Scenario: (-) Create Report with empty Name

       * def newReport =
        """
        {
        "name":"",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post4",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create Report with empty keyCode

       * def newReport =
        """
        {
        "name":"post Report05",
        "entity":"Account",
        "enabled":true,
        "keyCode":"",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "keyCode is required."



    Scenario: (-) Create Report with empty entity type

       * def newReport =
        """
        {
        "name":"post Report06",
        "entity":"",
        "enabled":true,
        "keyCode":"post6",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Entity is required."



    Scenario: (-) Create Report with empty Body

       * def newReport =
        """
        {
        "name":"post Report07",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post7",
        "description":"some description",
        "body":"",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Body is required."



    Scenario: (-) Create Report with keyCode starting with ish

       * def newReport =
        """
        {
        "name":"post Report09",
        "entity":"Account",
        "enabled":true,
        "keyCode":"ish.post9",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "User template code must not start with ish."



    Scenario: (-) Create Report with existing keyCode

#       <---> Add entity and define it's id:
       * def newReport =
        """
        {
        "name":"post Report11",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post11",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post Report11"])].id
        * print "id = " + id
#     <--->

        * def newReport =
         """
         {
         "name":"post Report11aaa",
         "entity":"Account",
         "enabled":true,
         "keyCode":"post11",
         "description":"some description",
         "body":"someBody",
         "subreport":false,
         "backgroundId":1,
         "sortOn":"amount",
         "preview":null,
         "variables":[],
         "options":[],
         }
         """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Report with incorrect 'options' values

       * def newReport =
        """
        {
        "name":"post Report13",
        "entity":"Account",
        "enabled":true,
        "keyCode":"post13",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName3", "label":"varLabel3", "type":"Date"}],
        "options":[{"name":"optName3","type":"Date time","value":"2019-01-03"}],
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "2019-01-03 is not valid for optName3 option"



    Scenario: (-) Create Report with Name >100 symbols

        * def newReport =
        """
        {
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"post14",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 chars."



    Scenario: (-) Create Report with keyCode >80 symbols

        * def newReport =
        """
        {
        "name":"post Report15",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "keyCode cannot be more than 80 chars."



    Scenario: (-) Create Report with not unique Variable name

        * def newReport =
        """
        {
        "name":"post Report16",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"post16",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName13", "label":"varLabel1", "type":"Text"},{"name":"varName13", "label":"varLabel1", "type":"Text"}],
        "options":[]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Option/Variable name should be unique: varname13"



    Scenario: (-) Create Report with not unique Option name

        * def newReport =
        """
        {
        "name":"post Report17",
        "entity":"AccountTransaction",
        "enabled":true,
        "keyCode":"post17",
        "description":"some description",
        "body":"someBody",
        "subreport":false,
        "backgroundId":1,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[{"name":"optName14","type":"Date","value":"2019-01-01"},{"name":"optName14","type":"Date","value":"2019-01-01"}]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 400
        And match $.errorMessage == "Option/Variable name should be unique: optname14"



