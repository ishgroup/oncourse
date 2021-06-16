@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/emailTemplate'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create EmailTemplate with type 'Email' by admin

        * def newEmailTemplate =
        """
        {
        "name":"post 1",
        "type":"Email",
        "entity":"Application",
        "enabled":true,
        "keyCode":"post1",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Text"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}]
        }
        """

        Given path ishPath
        And request newEmailTemplate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        And param pageSize = '1000'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post 1"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "type":"Email",
        "keyCode":"post1",
        "name":"post 1",
        "entity":"Application",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":
            [
            {"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}
            ],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description"
        }
        """

#       <---->  Scenario have been finished. Now delete created entity:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

    Scenario: (-) Create EmailTemplate with empty Name

        * def newEmailTemplate =
        """
        {
        "name":"",
        "type":"Sms",
        "entity":"Application",
        "enabled":true,
        "keyCode":"post4",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName3", "label":"varLabel3", "type":"Date"}],
        "options":[{"name":"optName3","type":"Checkbox","value":true}]
        }
        """

        Given path ishPath
        And request newEmailTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create new EmailTemplate for not existing entity

        * def newEmailTemplate =
        """
        {
        "name":"post 5",
        "type":"Sms",
        "entity":"notExistingEntity",
        "enabled":true,
        "keyCode":"post5",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName3", "label":"varLabel3", "type":"Date"}],
        "options":[{"name":"optName3","type":"Checkbox","value":true}]
        }
        """

        Given path ishPath
        And request newEmailTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Incorrect entity name"



    Scenario: (-) Create new EmailTemplate with Name length > 128 chars

        * def newEmailTemplate =
        """
        {
        "name":"post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6post6",
        "type":"Sms",
        "entity":"Application",
        "enabled":true,
        "keyCode":"post6",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Text"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}]
        }
        """

        Given path ishPath
        And request newEmailTemplate
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 chars."



    Scenario: (+) Create EmailTemplate with type 'Sms' by notadmin with access rights

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

        * def newEmailTemplate =
        """
        {
        "name":"post 3",
        "type":"Sms",
        "entity":"Application",
        "enabled":true,
        "keyCode":"post3",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName3", "label":"varLabel3", "type":"Date"}],
        "options":[{"name":"optName3","type":"Checkbox","value":true}]
        }
        """

        Given path ishPath
        And request newEmailTemplate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        And param pageSize = '1000'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["post 3"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "type":"Sms",
        "keyCode":"post3",
        "name":"post 3",
        "entity":"Application",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName3","label":"varLabel3","type":"Date","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName3","label":null,"type":"Checkbox","value":"true","system":null,"valueDefault":null}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description"
        }
        """

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new EmailTemplate by notadmin without access rights

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

        * def newEmailTemplate = {}

        Given path ishPath
        And request newEmailTemplate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create email template. Please contact your administrator"


