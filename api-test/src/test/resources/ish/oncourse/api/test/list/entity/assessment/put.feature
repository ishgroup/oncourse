@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/assessment'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/assessment'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update Assessment by admin

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code021",
        "name":"update assessment 21",
        "tags":[{"id":195}],
        "active":true,
        "description":"some description 21",
        "documents":[{"id":200}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code021"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code021UPD",
        "name":"update assessment 21UPD",
        "tags":[{"id":188}],
        "active":false,
        "description":"some description 21UPD",
        "documents":[{"id":201}]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "code":"code021UPD",
        "name":"update assessment 21UPD",
        "tags":[{"id":188,"name":"Short answer","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "active":false,
        "description":"some description 21UPD",
        "documents":"#ignore"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment Code to empty

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code024",
        "name":"update assessment 24",
        "tags":[],
        "active":true,
        "description":"some description 24",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code024"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"",
        "name":"update assessment 24",
        "tags":[],
        "active":true,
        "description":"some description 24",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment Name to empty

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code025",
        "name":"update assessment 25",
        "tags":[],
        "active":true,
        "description":"some description 25",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code025"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code025",
        "name":"",
        "tags":[],
        "active":false,
        "description":"some description 25",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment Code to >64 symbols

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code026",
        "name":"update assessment 26",
        "tags":[],
        "active":true,
        "description":"some description 26",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code026"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A64A6",
        "name":"update assessment 26",
        "tags":[],
        "active":true,
        "description":"some description 26",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code cannot be more than 64 chars."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment Name to >200 symbols

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code027",
        "name":"update assessment 27",
        "tags":[],
        "active":true,
        "description":"some description 27",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code027"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code027",
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A2",
        "tags":[],
        "active":false,
        "description":"some description 27",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name cannot be more than 200 chars."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment Code to existing

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code028",
        "name":"update assessment 28",
        "tags":[],
        "active":true,
        "description":"some description 28",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code028"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code1",
        "name":"update assessment 28",
        "tags":[],
        "active":true,
        "description":"some description 28",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment tag to not existing

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code029",
        "name":"update assessment 29",
        "tags":[],
        "active":true,
        "description":"some description 29",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code029"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code29",
        "name":"update assessment 29",
        "tags":[{"id":99999}],
        "active":true,
        "description":"some description 29",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Tag with id = 99999 doesn't exist."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment tag to not appropriate entity

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code030",
        "name":"update assessment 30",
        "tags":[],
        "active":true,
        "description":"some description 30",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code030"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code30",
        "name":"update assessment 30",
        "tags":[{"id":227}],
        "active":true,
        "description":"some description 30",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Tag with id = 227 is used for other entities."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment document to not existing

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code031",
        "name":"update assessment 31",
        "tags":[],
        "active":true,
        "description":"some description 31",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code031"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code31",
        "name":"update assessment 31",
        "tags":[],
        "active":true,
        "description":"some description 31",
        "documents":[{"id":99999}]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Document with id = 99999 doesn't exist."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Assessment to existing name

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code032",
        "name":"update assessment 32",
        "tags":[],
        "active":true,
        "description":"some description 32",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code032"])].id
        * print "id = " + id
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code32",
        "name":"assessment 1",
        "tags":[],
        "active":true,
        "description":"some description 32",
        "documents":[]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name must be unique."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Assessment

        * def assessmentToUpdate =
        """
        {
        "id":99999,
        "code":"code028",
        "name":"update assessment 28",
        "tags":[],
        "active":true,
        "description":"some description 28",
        "documents":[]
        }
        """

        Given path ishPath + '/99999'
        And request assessmentToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Update Assessment by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newAssessment =
        """
        {
        "code":"code022",
        "name":"update assessment 22",
        "tags":[{"id":195}],
        "active":true,
        "description":"some description 22",
        "documents":[{"id":200}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Assessment'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["code022"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def assessmentToUpdate =
        """
        {
        "id":"#(id)",
        "code":"code022UPD",
        "name":"update assessment 22UPD",
        "tags":[{"id":188}],
        "active":false,
        "description":"some description 22UPD",
        "documents":[{"id":201}]
        }
        """

        Given path ishPath + '/' + id
        And request assessmentToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "code":"code022UPD",
        "name":"update assessment 22UPD",
        "tags":[{"id":188,"name":"Short answer","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "active":false,
        "description":"some description 22UPD",
        "documents":"#ignore"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



