@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/faculty'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/faculty'
        * def ishPathList = 'list'




    Scenario: (+) Update faculty by admin

#       <----->  Add a new faculty for updating and define id:
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

        * def id = get[0] response.rows[?(@.values == ["[]",null,"FacultyName1","FacultyCode1","true"])].id
#       <--->

        * def facultyToUpdate =
        """
            {
              "id":"#(id)",
              "name": "UpdatedFacultyName1",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

        Given path ishPath + '/' + id
        And request facultyToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
            {
              "id": "#number",
              "name": "UpdatedFacultyName1",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204





    Scenario: (-) Update faculty required fields to empty

#       <----->  Add a new faculty for updating and define id:
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

        * def id = get[0] response.rows[?(@.values == ["[]",null,"FacultyName1","FacultyCode1","true"])].id

#       <--->  Update faculty to empty Name:
        * def facultyToUpdate =
        """
            {
              "name": "",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

        Given path ishPath + '/' + id
        And request facultyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <--->  Update faculty to empty Code:
        * def facultyToUpdate =
        """
            {
              "name": "FacultyName1",
              "code": "",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

        Given path ishPath + '/' + id
        And request facultyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."

 #       <--->  Scenario have been finished. Now find and remove created object from DB:
         * print "Scenario have been finished. Now find and remove created object from DB"

         Given path ishPath + '/' + id
         When method DELETE
         Then status 204



    Scenario: (-) Update faculty to not unique Code

#       <----->  Add a new faculty for updating and define id:
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

        * def id = get[0] response.rows[?(@.values == ["[]",null,"FacultyName1","FacultyCode1","true"])].id

#       <--->  Update faculty to not existent Code:
        * def facultyToUpdate =
        """
            {
              "name": "FacultyName1",
              "code": "testFacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

        Given path ishPath + '/' + id
        And request facultyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."

 #       <--->  Scenario have been finished. Now find and remove created object from DB:
         * print "Scenario have been finished. Now find and remove created object from DB"

         Given path ishPath + '/' + id
         When method DELETE
         Then status 204



    Scenario: (-) Update 'name' field length to out of range

#       <----->  Add a new faculty for updating and define id:
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

        * def id = get[0] response.rows[?(@.values == ["[]",null,"FacultyName1","FacultyCode1","true"])].id

#       <--->  Update Name to >200 symbols:
        * def facultyToUpdate =
        """
            {
              "name":"testFaculty_A2A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A1512A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A151",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """


        Given path ishPath + '/' + id
        And request facultyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Faculty name cannot be greater than 200 characters."

#       <--->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing faculty

        * def facultyToUpdate =
        """
            {
              "name":"FacultyName1",
              "code": "FacultyCode1",
              "webDescription": "webDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "tags": [],
              "documents": [],
              "relatedCourses": []
            }
        """

        Given path ishPath + '/99999'
        And request facultyToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
