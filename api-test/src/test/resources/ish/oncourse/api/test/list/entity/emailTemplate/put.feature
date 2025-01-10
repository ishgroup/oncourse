@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/emailTemplate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update EmailTemplate by admin

#       <----->  Add a new entity to update and define its id:
        * def newEmailTemplate =
        """
        {
        "name":"put 1",
        "type":"Post",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put1email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[],
        "options":[]
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

        * def id = get[0] response.rows[?(@.values == ["put 1"])].id
        * print "id = " + id
#       <--->

        * def emailTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "type":"Email",
        "name":"put 1 upd",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put1email",
        "subject":"some subject upd",
        "plainBody":"somePlainBodyUPD",
        "description":"some description upd",
        "body":"someBody upd",
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Text"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request emailTemplateToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "type":"Email",
        "keyCode":"put1email",
        "name":"put 1 upd",
        "entity":"Site",
        "subject":"some subject upd",
        "plainBody":"somePlainBodyUPD",
        "body":"someBody upd",
        "status":"Not Installed",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description upd",
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update EmailTemplate by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newEmailTemplate =
        """
        {
        "name":"put 2",
        "type":"Sms",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put2email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
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

        * def id = get[0] response.rows[?(@.values == ["put 2"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def emailTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "type":"Post",
        "name":"put 2 upd",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put2email",
        "subject":"some subject upd",
        "plainBody":"somePlainBodyUPD",
        "description":"some description upd",
        "body":"someBody upd",
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request emailTemplateToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "type":"Post",
        "keyCode":"put2email",
        "name":"put 2 upd",
        "entity":"Site",
        "subject":"some subject upd",
        "plainBody":"somePlainBodyUPD",
        "body":"someBody upd",
        "status":"Not Installed",
        "variables":[],
        "options":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description upd",
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update EmailTemplate by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newEmailTemplate =
        """
        {
        "name":"put 3",
        "type":"Email",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put3email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
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

        * def id = get[0] response.rows[?(@.values == ["put 3"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        Given path ishPath + '/' + id
        And request {}
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit email template. Please contact your administrator"

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update EmailTemplate to empty Name

#       <----->  Add a new entity to update and define its id:
        * def newEmailTemplate =
        """
        {
        "name":"put 4",
        "type":"Email",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put4email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
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

        * def id = get[0] response.rows[?(@.values == ["put 4"])].id
        * print "id = " + id

#       <--->  Update required field to empty:
        * def emailTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "type":"Email",
        "name":"",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put4email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request emailTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update EmailTemplate to not existing Entity

#       <----->  Add a new entity to update and define its id:
        * def newEmailTemplate =
        """
        {
        "name":"put 5",
        "type":"Email",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put5email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
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

        * def id = get[0] response.rows[?(@.values == ["put 5"])].id
        * print "id = " + id
#       <--->

        * def emailTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "type":"Email",
        "name":"put 5",
        "entity":"notExistingEntity",
        "status":"Enabled",
        "keyCode":"put5email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request emailTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Incorrect entity name"

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing EmailTemplate

#       <----->  Add a new entity to update and define its id:
        * def emailTemplateToUpdate =
        """
        {
        "id":99999,
        "type":"Email",
        "name":"put 6",
        "entity":"notExistingEntity",
        "status":"Enabled",
        "keyCode":"put6email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1","label":"varLabel1","type":"Text","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1","label":null,"type":"Date","value":"2019-01-01","system":null,"valueDefault":null}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/99999'
        And request emailTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Update EmailTemplate configs by admin

#       <----->  Add a new entity to update and define its id:
        * def newEmailTemplate =
        """
        {
        "name":"put 1",
        "type":"Post",
        "entity":"Application",
        "status":"Enabled",
        "keyCode":"put1email",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[],
        "options":[]
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

        * def id = get[0] response.rows[?(@.values == ["put 1"])].id
        * print "id = " + id
#       <--->

        * def emailTemplateConfigsToUpdate =
        """
        {
            "config":'  name: \"Updated Email template\"\n  short: Updated email template short\n  description: \"Updated email template description\"\n  subject: \"Updated subject\"'
        }
        """

        Given path ishPath + '/config/' + id
        And request emailTemplateConfigsToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/config/' + id
        When method GET
        Then status 200
        And match $ contains 'shortDescription: Updated email template description\ndescription: Updated email template description\nname: Updated Email template\nentityClass: Application\ntype: POST\nsubject: Updated subject'

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204