@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/report'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/report'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Get report by admin

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['Certificate'])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ ==
        """
        {
        "id":"#(~~id)",
        "name":"Certificate",
        "entity":"Certificate",
        "enabled":true,
        "keyCode":"ish.onCourse.certificate",
        "description":"onCourse includes AQF recommended templates for full Qualification Certificates, Statements of Attainment and transcripts. Certificates can only be generated from units that are recorded as part of onCourse enrolments.This report prints in Portrait format.",
        "body":"#present",
        "subreport":true,
        "backgroundId":null,
        "sortOn":"",
        "preview":null,
        "variables":[],
        "options":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Get report by notadmin

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['Classes'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        And match $ ==
        """
        {
        "id":"#(~~id)",
        "name":"Classes",
        "entity":"CourseClass",
        "enabled":true,
        "keyCode":"ish.onCourse.classListReport",
        "description":"To obtain an overview of all classes status within a given time period, such as a term.This report prints in Landscape format.",
        "body":"#present",
        "subreport":true,
        "backgroundId":null,
        "sortOn":"",
        "preview":null,
        "variables":[],
        "options":[],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (-) Get not existing report

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."