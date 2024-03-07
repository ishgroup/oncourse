@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/faculty'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/faculty'
        * def ishPathList = 'list'




    Scenario: (+) Create faculty by admin

        * def newFaculty =
        """
            {
              "name": "FacultyName1",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

        Given path ishPath
        And request newFaculty
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'Faculty'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["FacultyName1","FacultyCode1","true"]

        * def id = get[0] response.rows[?(@.values == ["[]",null,"FacultyName1","FacultyCode1","true"])].id

#       <---> Assertion:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
            {
              "id": "#number",
              "name": "FacultyName1",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "createdOn": "#ignore",
              "modifiedOn": "#ignore",
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204


    Scenario: (-) Create faculty with empty Name

        * def newFaculty =
        """
        {
        "name": "",
	    "code": "FacultyCode1",
        "webDescription": "webDescription1",
        "shortWebDescription": "shortWebDescription1",
	    "isShownOnWeb": true
        }
        """

        Given path ishPath
        And request newFaculty
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."

    Scenario: (-) Create faculty with empty Code

        * def newFaculty =
        """
        {
        "name": "TestName123",
	    "code": "",
        "webDescription": "webDescription1",
        "shortWebDescription": "shortWebDescription1",
	    "isShownOnWeb": true
        }
        """

        Given path ishPath
        And request newFaculty
        When method POST
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create faculty with existing 'Code'

        * def newFaculty =
        """
        {
        "name": "testName123",
	    "code": "testFacultyCode1",
        "webDescription": "webDescription1",
        "shortWebDescription": "shortWebDescription1",
	    "isShownOnWeb": true
        }
        """

        Given path ishPath
        And request newFaculty
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."




    Scenario: (-) Create faculty with Code > 32 symbols

        * def newFaculty =
        """
        {
        "name": "testName123",
	    "code": "testCode123456789012345678901234567890",
        "webDescription": "webDescription1",
        "shortWebDescription": "shortWebDescription1",
	    "isShownOnWeb": true
        }
        """

        Given path ishPath
        And request newFaculty
        When method POST
        Then status 400
        And match $.errorMessage == "Faculty code cannot be greater than 32 characters."





    Scenario: (-) Create faculty with Name >200 symbols

        * def newFaculty =
        """
        {
        "name": "_A2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A15193A96A100A104A108A112A116A120A124A128A132A136A140A144A148A151",
	    "code": "testCode123",
        "webDescription": "webDescription1",
        "shortWebDescription": "shortWebDescription1",
	    "isShownOnWeb": true
        }
        """

        Given path ishPath
        And request newFaculty
        When method POST
        Then status 400
        And match $.errorMessage == "Faculty name cannot be greater than 200 characters."

