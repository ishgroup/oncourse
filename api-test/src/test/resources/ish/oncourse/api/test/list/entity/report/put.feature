@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/report'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/report'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Update custom Report by admin

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report01",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put1",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName2", "label":"varLabel2", "type":"Checkbox"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report01'])].id
        * print "id = " + id
#       <--->

        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report01 UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put1",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[{"name":"varName2UPD", "label":"varLabel2UPD", "type":"Record"}],
        "options":[{"name":"optName1UPD","type":"Date","value":"2025-05-05"}]
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "name":"put Report01 UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put1",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[{"name":"varName2UPD", "label":"varLabel2UPD", "type":"Record","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1UPD","label":null,"type":"Date","value":"2025-05-05","system":null,"valueDefault":null}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update custom report by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report02",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put2",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName1", "label":"varLabel1", "type":"Record"},{"name":"varName2", "label":"varLabel2", "type":"Date"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report02'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report02 UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put2",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[{"name":"varName1UPD", "label":"varLabel1UPD", "type":"Date time"}],
        "options":[{"name":"optName1UPD","type":"Date","value":"2025-05-05"}],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "name":"put Report02 UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put2",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[{"name":"varName1UPD", "label":"varLabel1UPD", "type":"Date time","value":null,"system":null,"valueDefault":null}],
        "options":[{"name":"optName1UPD","label":null,"type":"Date","value":"2025-05-05","system":null,"valueDefault":null}],
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "shortDescription":null,
        "automationTags":null,
        "category":null

        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report03",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put3",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report03'])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report03 UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put3",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit report template. Please contact your administrator"

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report to empty Name

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report04",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put4",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report04'])].id
        * print "id = " + id

#       <--->  Update report to empty Name:
        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put4",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report to empty keyCode

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report05",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put5",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report05'])].id
        * print "id = " + id

#       <--->  Update report to empty Name:
        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report05",
        "entity":"AccountTransaction",
        "status":"Not Installed",
        "keyCode":"",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Key code is read only"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report keyCode to other value

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report06",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put6",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report06'])].id
        * print "id = " + id

#       <--->  Update report to empty Name:
        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report06",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"other",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Key code is read only"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report to empty Entity

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report07",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put7",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report07'])].id
        * print "id = " + id

#       <--->  Update report to empty Name:
        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report07",
        "entity":null,
        "status":"Not Installed",
        "keyCode":"put7",
        "description":"some description",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Entity is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report to empty Body

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report08",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put8",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report08'])].id
        * print "id = " + id

#       <--->  Update report to empty Name:
        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report08",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put8",
        "description":"some description",
        "body":"",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Body is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report Name to >100 symbols

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report11",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put11",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report11'])].id
        * print "id = " + id
#       <--->

        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A1",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put11",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name cannot be more than 100 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update custom report Entity to >40 symbols

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report03",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put3",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report03'])].id
        * print "id = " + id
#        <--->

        * def reportToUpdate =
        """
        {
        "id":"#(id)",
        "name":"put Report03 UPD",
        "entity":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42",
        "status":"Not Installed",
        "keyCode":"put3",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Entity cannot be more than 40 chars."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update system report

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['Certificate'])].id
        * print "id = " + id

        * def reportToUpdate =
        """
        {
        "id":1,
        "name":"Certificate UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"ish.onCourse.certificate",
        "description":"UPD onCourse includes AQF recommended templates for full Qualification Certificates, Statements of Attainment and transcripts. Certificates can only be generated from units that are recorded as part of onCourse enrolments.This report prints in Portrait format.",
        "body":"UPD",
        "backgroundId":null,
        "sortOn":"",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/' + id
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "This is a internal template which cannot be edited."



    Scenario: (-) Update not existing custom report

        * def reportToUpdate =
        """
        {
        "id":99999,
        "name":"put Report03 UPD",
        "entity":"Site",
        "status":"Not Installed",
        "keyCode":"put3",
        "description":"some description UPD",
        "body":"someBody UPD",
        "backgroundId":null,
        "sortOn":"name",
        "preview":null,
        "variables":[],
        "options":[],
        "shortDescription":null,
        "automationTags":null,
        "category":null
        }
        """

        Given path ishPath + '/99999'
        And request reportToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."


    Scenario: (+) Update custom Report configs by admin

#       <----->  Add a new entity to update and define its id:
        * def newReport =
        """
        {
        "name":"put Report01",
        "entity":"AccountTransaction",
        "status":"Enabled",
        "keyCode":"put1",
        "description":"some description",
        "body":"someBody",
        "backgroundId":null,
        "sortOn":"amount",
        "preview":null,
        "variables":[{"name":"varName2", "label":"varLabel2", "type":"Checkbox"}],
        "options":[{"name":"optName1","type":"Date","value":"2019-01-01"}]
        }
        """

        Given path ishPath
        And request newReport
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Report'
        And param pageSize = 65000
        And param offset = 0
        And param columns = 'name'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ['put Report01'])].id
        * print "id = " + id
#       <--->

        * def reportToUpdate =
        """
        {
            "config":'  name: \"Updated Training Plan Report\"\n  short:\n  description: \"Updated description\"\n  category:\n  sortOn: \"\"\n  isVisible: false'
        }
        """

        Given path ishPath + '/config/' + id
        And request reportToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/config/' + id
        When method GET
        Then status 200
        And match $ contains 'shortDescription: Updated description'
        And match $ contains 'description: Updated description'
        And match $ contains 'name: Updated Training Plan Report'
        And match $ contains 'entityClass: AccountTransaction'
        And match $ contains 'sortOn: amount'
        And match $ contains 'isVisible: false'

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



