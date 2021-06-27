@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/exportTemplate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/exportTemplate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


        
    Scenario: (+) Delete custom ExportTemplate by admin

#       <----->  Add a new entity for deleting and get id:
        * def newExportTemplate =
        """
        {
        "name":"delete ExportTemplate01",
        "keyCode":"delete01",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"xml"
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

        * def id = get[0] response.rows[?(@.values == ["delete ExportTemplate01"])].id
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



    Scenario: (+) Delete custom ExportTemplate by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newExportTemplate =
        """
        {
        "name":"delete ExportTemplate02",
        "keyCode":"delete02",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"xml"
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

        * def id = get[0] response.rows[?(@.values == ["delete ExportTemplate02"])].id
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



    Scenario: (-) Delete custom ExportTemplate by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newExportTemplate =
        """
        {
        "name":"delete ExportTemplate03",
        "keyCode":"delete03",
        "entity":"AccountTransaction",
        "body":"someBody",
        "enabled":true,
        "variables":[],
        "options":[],
        "outputType":"xml"
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

        * def id = get[0] response.rows[?(@.values == ["delete ExportTemplate03"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete export template. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete system ExportTemplate

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Template provided by ish cannot be deleted."



    Scenario: (-) Delete NOT existing ExportTemplate

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

