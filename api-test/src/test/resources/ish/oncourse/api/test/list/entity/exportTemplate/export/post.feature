@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/exportTemplate/export'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate/export'
        * def ishPathExportTemplate = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Export system Template by admin

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Class outcomes CSV export"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        And request {}
        When method POST
        Then status 200
        And match $ contains '"studentNumber": o.enrolment.student.studentNumber'
        And match $ contains '"firstName"    : o.enrolment.student.contact.firstName'
        And match $ contains '"lastName"     : o.enrolment.student.contact.lastName'
        And match $ contains '"classCode"    : "${o.enrolment.courseClass.course.code}-${o.enrolment.courseClass.code}"'
        And match $ contains '"nationalCode" : o.module?.nationalCode'



    Scenario: (+) Export custom Template by admin

#       <----->  Add a new entity to export and define its id:
        * def newExportTemplate =
        """
        {
        "name":"ExportTemplate1",
        "keyCode":"code01",
        "entity":"Site",
        "body":"someBody1",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":null
        }
        """

        Given path ishPathExportTemplate
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["ExportTemplate1"])].id
        * print "id = " + id
#       <--->

        Given path ishPath + '/' + id
        And request {}
        When method POST
        Then status 200
        And match $ == "someBody1"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPathExportTemplate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Export system Template by notadmin

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Class outcomes CSV export"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        And request {}
        When method POST
        Then status 200
        And match $ contains '"studentNumber": o.enrolment.student.studentNumber'
        And match $ contains '"firstName"    : o.enrolment.student.contact.firstName'
        And match $ contains '"lastName"     : o.enrolment.student.contact.lastName'
        And match $ contains '"classCode"    : "${o.enrolment.courseClass.course.code}-${o.enrolment.courseClass.code}"'
        And match $ contains '"nationalCode" : o.module?.nationalCode'



    Scenario: (+) Export custom Template by notadmin

#       <----->  Add a new entity to export and define its id:
        * def newExportTemplate =
        """
        {
        "name":"ExportTemplate2",
        "keyCode":"code02",
        "entity":"Room",
        "body":"someBody2",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "description":null
        }
        """

        Given path ishPathExportTemplate
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["ExportTemplate2"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        And request {}
        When method POST
        Then status 200
        And match $ == "someBody2"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathExportTemplate + '/' + id
        When method DELETE
        Then status 204



#    Scenario: (-) Export not existing Template
#
#        Given path ishPath + '/99999'
#        And request {}
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Record with id = '99999' doesn't exist."
