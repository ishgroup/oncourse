@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/exportTemplate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


#       <------------------> List for this entity is not supported.

#    Scenario: (+) Get list of all Templates by admin
#
#        Given path ishPathList
#        And param entity = 'ExportTemplate'
#        When method GET
#        Then status 200
#        And match $.rows[*].id contains ["1", "2"]
#
#
#
#    Scenario: (+) Get list of all Templates by notadmin with access rights
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
#        Given path ishPathList
#        And param entity = 'ExportTemplate'
#        When method GET
#        Then status 200
#        And match $.rows[*].id contains ["1", "2"]

#       <-------------------------------------------->


    Scenario: (+) Get Template by admin

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Room CSV export"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":null
        }
        """



    Scenario: (+) Get Template by notadmin

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

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Class outcomes CSV export"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "name":"Class outcomes CSV export",
        "keyCode":"ish.onCourse.classOutcomes.csv",
        "entity":"CourseClass",
        "body":"records.collectMany { CourseClass cc -> cc.enrolments }.collectMany { e -> e.outcomes }.each { o ->\n\tcsv << [\n\t\t\t\"studentNumber\": o.enrolment.student.studentNumber,\n\t\t\t\"firstName\"    : o.enrolment.student.contact.firstName,\n\t\t\t\"lastName\"     : o.enrolment.student.contact.lastName,\n\t\t\t\"classCode\"    : \"${o.enrolment.courseClass.course.code}-${o.enrolment.courseClass.code}\",\n\t\t\t\"nationalCode\" : o.module?.nationalCode,\n\t\t\t\"title\"        : o.module?.title,\n\t\t\t\"startDate\"    : o.startDate?.format(\"d/M/Y hh:mm a\"),\n\t\t\t\"endDate\"      : o.endDate?.format(\"d/M/Y hh:mm a\"),\n\t\t\t\"status\"       : o.status?.displayName\n\t]\n}\n",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":null
        }
        """



    Scenario: (-) Get not existing Template

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

