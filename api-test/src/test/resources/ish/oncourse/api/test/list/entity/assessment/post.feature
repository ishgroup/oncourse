@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/assessment'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/assessment'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'




    Scenario: (+) Create Assessment by admin

        * def newAssessment =
        """
        {
        "code":"code001",
        "name":"create assessment 1",
        "tags":[195],
        "active":true,
        "gradingTypeId":1,
        "description":"some description 1",
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

        * def id = get[0] response.rows[?(@.values == ["code001"])].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "code":"code001",
        "name":"create assessment 1",
        "gradingTypeId":1,
        "tags":[195],
        "active":true,
        "description":"some description 1",
        "documents":
        [
            {"shared":true,
            "thumbnail":null,
            "access":"Private",
            "added":"#ignore",
            "description":"Private description",
            "createdOn":"#ignore",
            "tags":[],
            "attachmentRelations":"#ignore",
            "modifiedOn":"#ignore",
            "removed":false,
            "attachedRecordsCount":null,
            "versions":
                [
                    {"fileName":"defaultPrivateDocument.txt","thumbnail":null,"size":"22 b","added":"#ignore","createdBy":"onCourse Administrator","id":200,"mimeType":"text\/plain","url":"#string","content":null,current:true}
                ],
            "name":"defaultPrivateDocument",
            "id":200,
            "urlWithoutVersionId":"#string"}]
        }
        """

#       <---->  Scenario have been finished. Now remove created entity:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new Assessment with empty Code

        * def newAssessment =
        """
        {
        "code":"",
        "name":"create assessment 4",
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create new Assessment with empty Name

        * def newAssessment =
        """
        {
        "code":"code005",
        "name":"",
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create new Assessment with existing code

        * def newAssessment =
        """
        {
        "code":"code1",
        "name":"create assessment 6",
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Create new Assessment with not existing tag

        * def newAssessment =
        """
        {
        "code":"code007",
        "name":"create assessment 7",
        "tags":[99999],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Tag with id = 99999 doesn't exist."



    Scenario: (-) Create new Assessment with tag for other entity

        * def newAssessment =
        """
        {
        "code":"code007a",
        "name":"create assessment 7",
        "tags":[227],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Tag with id = 227 is used for other entities."



    Scenario: (-) Create new Assessment with not existing document

        * def newAssessment =
        """
        {
        "code":"code008",
        "name":"create assessment 8",
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[{"id":99999}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Document with id = 99999 doesn't exist."



    Scenario: (-) Create new Assessment with code >64 symbols

        * def newAssessment =
        """
        {
        "code":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A64A6",
        "name":"create assessment 9",
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Code cannot be more than 64 chars."



    Scenario: (-) Create new Assessment with name >200 symbols

        * def newAssessment =
        """
        {
        "code":"code008",
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A2",
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Name cannot be more than 200 chars."



    Scenario: (-) Create Assessment with existing name

        * def newAssessment =
        """
        {
        "code":"dghrhf",
        "name":"assessment 1",
        "tags":[195],
        "active":true,
        "description":"some description 1",
        "documents":[{"id":200}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Name must be unique."



    Scenario: (-) Create Assessment with tag relation directly to a tag group

        * def newAssessment =
        """
        {
        "code":"code00183",
        "name":"create assessment 11",
        "tags":[199],
        "active":true,
        "description":"some description 1111",
        "documents":[{"id":200}]
        }
        """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Tag relations cannot be directly related to a tag group."



    Scenario: (+) Create Assessment by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * def newAssessment =
        """
        {
        "code":"code002",
        "name":"create assessment 2",
        "tags":[195],
        "active":true,
        "description":"some description 2",
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

        * def id = get[0] response.rows[?(@.values == ["code002"])].id
        * print "id = " + id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "code":"code002",
        "name":"create assessment 2",
        "gradingTypeId":null,
        "tags":[195],
        "active":true,
        "description":"some description 2",
        }
        """

#       <---->  Scenario have been finished. Now remove created entity:
        * configure headers = { Authorization:  'admin'}



        Given path ishPath + '/' + id
        When method DELETE
        Then status 204
