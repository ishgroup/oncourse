@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/emailTemplate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing EmailTemplate by admin

#       <----->  Add a new entity for deleting and get id:
        * def newEmailTemplate =
        """
        {
        "name":"del 1",
        "type":"Email",
        "entity":"Application",
        "enabled":true,
        "keyCode":"del1",
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

        * def id = get[0] response.rows[?(@.values == ["del 1"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing EmailTemplate by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newEmailTemplate =
        """
        {
        "name":"del 2",
        "type":"Post",
        "entity":"Application",
        "enabled":true,
        "keyCode":"del2",
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

        * def id = get[0] response.rows[?(@.values == ["del 2"])].id
        * print "id = " + id

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete existing EmailTemplate by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newEmailTemplate =
        """
        {
        "name":"del 3",
        "type":"Email",
        "entity":"Application",
        "enabled":true,
        "keyCode":"del3",
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

        * def id = get[0] response.rows[?(@.values == ["del 3"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete email template. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing EmailTemplate

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."


