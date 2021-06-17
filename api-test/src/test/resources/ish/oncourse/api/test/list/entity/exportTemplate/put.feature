@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/exportTemplate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update custom Export template by admin

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate01",
        "keyCode":"500",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName10", "label":"varLabel10", "type":"Text"}],
        "options":[{"name":"optName10", "type":"Date time", "value":"2019-01-01T22:00:00.000Z"}],
        "outputType":"json",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate01"])].id
        * print "id = " + id
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate01UPD",
        "keyCode":"500",
        "entity":"AccountTransactionUPD",
        "body":"someBodyUPD",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description upd"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"put ExportTemplate01UPD",
        "keyCode":"500",
        "entity":"AccountTransactionUPD",
        "body":"someBodyUPD",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description upd"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update custom template by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate02",
        "keyCode":"501",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[{"name":"varName11", "label":"varLabel11", "type":"Text"}],
        "options":[],
        "outputType":"json",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate02"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate02UPD",
        "keyCode":"501",
        "entity":"AccountTransactionUPD",
        "body":"someBodyUPD",
        "enabled":false,
        "variables":[{"name":"varName11","label":"varLabel11","type":"Text"},{"name":"varName11a","label":"varLabel11a","type":"Date"}],
        "options":[{"name":"optName11", "type":"Text", "value":"some record"}],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "name":"put ExportTemplate02UPD",
        "keyCode":"501",
        "entity":"AccountTransactionUPD",
        "body":"someBodyUPD",
        "enabled":false,
        "variables":[{"name":"varName11","label":"varLabel11","type":"Text","value":null,"system":null,"valueDefault":null},{"name":"varName11a","label":"varLabel11a","type":"Date","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName11", "label":null, "type":"Text", "value":"some record","system":null,"valueDefault":null}],
        "outputType":"txt",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "description":"some description"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate03",
        "keyCode":"502",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"csv",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate03"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate03UPD",
        "keyCode":"502",
        "entity":"AccountTransactionUPD",
        "body":"someBodyUPD",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit export template. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}
        
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template to empty Name

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate04",
        "keyCode":"504",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"xml",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate04"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "keyCode":"504",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"xml",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template to empty keyCode

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate05",
        "keyCode":"505",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"ics",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate05"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate05",
        "keyCode":"",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Key code is read only"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template keyCode to other value

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate05a",
        "keyCode":"505a",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"ics",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate05a"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate05",
        "keyCode":"other",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":false,
        "variables":[],
        "options":[],
        "outputType":"ics",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Key code is read only"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template to empty Entity

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate06",
        "keyCode":"506",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate06"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate06",
        "keyCode":"506",
        "entity":"",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Entity is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template to empty Body

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate07",
        "keyCode":"507",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate07"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate07",
        "keyCode":"507",
        "entity":"AccountTransaction",
        "body":"",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Body is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template to empty Output type

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate07a",
        "keyCode":"507a",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate07a"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate07a",
        "keyCode":"507a",
        "entity":"AccountTransaction",
        "body":"some body",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":null,
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Output type is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template to not existing Output type

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate07b",
        "keyCode":"507b",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate07b"])].id
        * print "id = " + id

#       <--->  Update template to empty Name:
        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate07b",
        "keyCode":"507b",
        "entity":"AccountTransaction",
        "body":"some body",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"exe",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Output type is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template Name to >100 symbols

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate11",
        "keyCode":"511",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate11"])].id
        * print "id = " + id
#       <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "keyCode":"511",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom template Entity to >40 symbols

#       <----->  Add a new entity to update and define its id:
        * def newExportTemplate =
        """
        {
        "name":"put ExportTemplate13",
        "keyCode":"513",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
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

        * def id = get[0] response.rows[?(@.values == ["put ExportTemplate13"])].id
        * print "id = " + id
#        <--->

        * def exportTemplateToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put ExportTemplate13",
        "keyCode":"513",
        "entity":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/' + id
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Entity cannot be more than 40 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update system template

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
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/1'
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "This is a internal template which cannot be edited."



    Scenario: (-) Update not existing custom template

        * def exportTemplateToUpdate =
        """
        {
        "id":99999,
        "name":"put ExportTemplate13",
        "keyCode":"513",
        "entity":"Site",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"txt",
        "description":"some description"
        }
        """

        Given path ishPath + '/99999'
        And request exportTemplateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



