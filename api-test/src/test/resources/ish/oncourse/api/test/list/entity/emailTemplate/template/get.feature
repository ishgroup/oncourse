@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/emailTemplate/template'

    Background: Authorize first
        * call read('../../../../signIn.feature')
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
#        And match $.rows[*].id contains
#        """
#
#        """

#       <---->  Scenario have been finished. Now delete created entity:
        Given path ishPathTemplate + '/' + id
        When method DELETE
        Then status 204




#    Scenario: (+) Get list of all EmailTemplates by notadmin with access rights
#
##       <--->  Login as notadmin
#        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}
#
#        Given path '/login'
#        And request loginBody
#        When method PUT
#        Then status 200
##       <--->
#
#        Given path ishPathList
#        And param entity = 'EmailTemplate'
#        When method GET
#        Then status 200
#        And match $.rows[*].id contains ["1"]



#    Scenario: (+) Get EmailTemplate by admin
#
#        Given path ishPath + '/34'
#        When method GET
#        Then status 200
#        And match $.id == 1
#
#
#
#    Scenario: (+) Get EmailTemplate by notadmin
#
##       <--->  Login as notadmin
#        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}
#
#        Given path '/login'
#        And request loginBody
#        When method PUT
#        Then status 200
##       <--->
#
#        Given path ishPath + '/2'
#        When method GET
#        Then status 200
#        And match $.id == 2
#
#
#
#    Scenario: (-) Get not existing EmailTemplate
#
#        Given path ishPath + "/99999"
#        When method GET
#        Then status 400
#        And match $.errorMessage == "Record with id = '99999' doesn't exist."
