@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/emailTemplate/template'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/emailTemplate/template'
        * def ishPathTemplate = 'list/entity/emailTemplate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Get list of all EmailTemplates by admin

        * def newEmailTemplate =
        """
        {
        "name":"template 1",
        "type":"Email",
        "entity":"Course",
        "enabled":true,
        "keyCode":"template1",
        "subject":"some subject",
        "plainBody":"somePlainBody",
        "description":"some description",
        "body":"someBody",
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Text"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}]
        }
        """

        Given path ishPathTemplate
        And request newEmailTemplate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'EmailTemplate'
        And param columns = 'name'
        And param pageSize = '1000'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["template 1"])].id
        * print "id = " + id
#       <--->

        Given path ishPath
        And param entity = 'Course'
        When method GET
        Then status 200

#       <---->  Scenario have been finished. Now delete created entity:
        Given path ishPathTemplate + '/' + id
        When method DELETE
        Then status 204


