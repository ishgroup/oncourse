@parallel=false
Feature: Main feature for all PATCH requests with path 'list/entity/exportTemplate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update system ExportTemplate by admin

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Room CSV export"])].id
        * print "id = " + id

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":null
        }
        """

#       <--->  Scenario have been finished. Now change back object to default:
        * def exportTemplateToDefault =
        """
        {
        "id":"#(id)",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request exportTemplateToDefault
        When method PATCH
        Then status 204



    Scenario: (+) Update custom ExportTemplate by admin

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"patch ExportTemplate01",
        "keyCode":"601",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[{"name":"optName110", "type":"Text", "value":"some record"}],
        "outputType":"txt"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["patch ExportTemplate01"])].id
        * print "id = " + id
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"patch ExportTemplate01a",
        "keyCode":"601a",
        "entity":"AccountTransaction",
        "body":"someBodyUPD",
        "enabled":true,
        "variables":[],
        "options":[{"name":"optName110", "type":"Text", "value":"some record UPD"}],
        "outputType":"txt"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"patch ExportTemplate01",
        "keyCode":"601",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[{"name":"optName110", "label":null, "type":"Text", "value":"some record UPD","system":null,"valueDefault":null}],
        "outputType":"txt",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":null
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update system ExportTemplate by notadmin with access rights

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Room CSV export"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":null
        }
        """

#       <--->  Scenario have been finished. Now change back object to default:
        * def exportTemplateToDefault =
        """
        {
        "id":"#(id)",
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request exportTemplateToDefault
        When method PATCH
        Then status 204



    Scenario: (+) Update custom ExportTemplate by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"patch ExportTemplate02",
        "keyCode":"602",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[{"name":"optName111", "type":"Text", "value":"some record"}],
        "outputType":"json"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["patch ExportTemplate02"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"patch ExportTemplate02a",
        "keyCode":"602a",
        "entity":"AccountTransaction",
        "body":"someBodyUPD",
        "enabled":true,
        "variables":[],
        "options":[{"name":"optName111", "type":"Text", "value":"some record UPD"}],
        "outputType":"json"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"patch ExportTemplate02",
        "keyCode":"602",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[{"name":"optName111", "label":null, "type":"Text", "value":"some record UPD","system":null,"valueDefault":null}],
        "outputType":"json",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":null
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update system ExportTemplate by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":1,
        "name":"Room CSV export",
        "keyCode":"ish.onCourse.room.csv",
        "entity":"Room",
        "body":"records.each { Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update export template. Please contact your administrator"



    Scenario: (-) Update system ExportTemplate disabled fields

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Room CSV export"])].id
        * print "id = " + id

#       <---> Values should not be changed:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"Room CSV export UPD",
        "keyCode":"ish.onCourse.room.csv.UPD",
        "entity":"Site",
        "body":"records.each {UPD Room r ->\n\tcsv << [\n\t\t\t'name'                         : r.name,\n\t\t\t'seated capacity'              : r.seatedCapacity,\n\t\t\t'directions'                   : r.directions,\n\t\t\t'facilities'                   : r.facilities,\n\t\t\t'notes'                        : r.notes,\n\t\t\t'created on'                   : r.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'modified on'                  : r.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'siteName'                     : r.site.name,\n\t\t\t'site is administration centre': r.site.isAdministrationCentre,\n\t\t\t'site Latitude'                : r.site.latitude,\n\t\t\t'site Longitude'               : r.site.longitude,\n\t\t\t'site Postcode'                : r.site.postcode,\n\t\t\t'site State'                   : r.site.state,\n\t\t\t'site Street'                  : r.site.street,\n\t\t\t'site Suburb'                  : r.site.suburb,\n\t\t\t'site Created on'              : r.site.createdOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\"),\n\t\t\t'site Modified on'             : r.site.modifiedOn?.format(\"yyyy-MM-dd'T'HH:mm:ssXXX\")\n\t]\n}\n",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"csv"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
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
        "outputType":"csv"
        }
        """



    Scenario: (-) Update custom ExportTemplate disabled fields

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"patch ExportTemplate03",
        "keyCode":"603",
        "entity":"Room",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"ics"
        }
        """

        Given path ishPath
        And request newExportTemplate
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'ExportTemplate'
        And param columns = 'name'
        And param offset = 50
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["patch ExportTemplate03"])].id
        * print "id = " + id
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"patch ExportTemplate03UPD",
        "keyCode":"603UPD",
        "entity":"Account",
        "body":"someBody UPD",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"ics"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 204

#       <---> Assertion: Values should not be changed
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"patch ExportTemplate03",
        "keyCode":"603",
        "entity":"Room",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"ics",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Export template

        * def exportTemplateToUpdate =
        """
        {
        "id":99999,
        "name":"patch ExportTemplate10",
        "keyCode":"610",
        "entity":"Account",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"txt"
        }
        """

        Given path ishPath
        And request exportTemplateToUpdate
        When method PATCH
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."