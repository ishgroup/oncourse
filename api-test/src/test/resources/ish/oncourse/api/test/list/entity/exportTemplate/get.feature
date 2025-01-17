@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/exportTemplate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        

    Scenario: (+) Get Template by admin

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param pageSize = 100
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Room CSV"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "name":"Room CSV",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "status":"Enabled",
        "variables":[],
        "options":[],
        "outputType":"csv",
        "description":"Export records to CSV format, suitable for spreadsheets or other applications that require a flat file. Choose a different format for long text or structured data.",
        "shortDescription":"Open a list of Rooms in a spreadsheet",
        "automationTags":null,
        "category":"scheduling"
        }
        """



    Scenario: (+) Get Template by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Class outcomes CSV"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "name":"Class outcomes CSV",
        "keyCode":"ish.onCourse.classOutcomes.csv",
        "entity":"CourseClass",
        "body":"records.collectMany { CourseClass cc -> cc.enrolments }.collectMany { e -> e.outcomes }.each { o ->\n\tcsv << [\n\t\t\t\"studentNumber\": o.enrolment.student.studentNumber,\n\t\t\t\"firstName\"    : o.enrolment.student.contact.firstName,\n\t\t\t\"lastName\"     : o.enrolment.student.contact.lastName,\n\t\t\t\"classCode\"    : \"${o.enrolment.courseClass.course.code}-${o.enrolment.courseClass.code}\",\n\t\t\t\"nationalCode\" : o.module?.nationalCode,\n\t\t\t\"title\"        : o.module?.title,\n\t\t\t\"startDate\"    : o.startDate?.format(\"d/M/Y hh:mm a\"),\n\t\t\t\"endDate\"      : o.endDate?.format(\"d/M/Y hh:mm a\"),\n\t\t\t\"status\"       : o.status?.displayName\n\t]\n}\n",
        "status":"Enabled",
        "variables":[],
        "options":[],
        "outputType":"csv",
        "description":"Export records to CSV format, suitable for spreadsheets or other applications that require a flat file. Choose a different format for long text or structured data.",
        "shortDescription":"Open a list of Class outcomess in a spreadsheet",
        "automationTags":null,
        "category":"education"
        }
        """


    Scenario: (+) Get Configs of template by admin

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param pageSize = 100
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Room CSV"])].id
        * print "id = " + id

        Given path ishPath + '/config/' + id
        When method GET
        Then status 200
        And match $ contains 'shortDescription: "Export records to CSV format, suitable for spreadsheets or other\\\n  \\ applications that require a flat file. Choose a different format for long text\\\n  \\ or structured data."\ndescription: "Export records to CSV format, suitable for spreadsheets or other applications\\\n  \\ that require a flat file. Choose a different format for long text or structured\\\n  \\ data."\ncategory: scheduling\nname: Room CSV\nentityClass: Room\noutputType: CSV'

    Scenario: (-) Get not existing Template

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

