@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/faculty'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/faculty'
        * def ishPathList = 'list'

#
#

    Scenario: (+) Delete existing faculty

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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPathList
        And param entity = 'Faculty'
        When method GET
        Then status 200
        And match $.rows[*].values[*] !contains ["FacultyName1"]


    Scenario: (-) Delete faculty with assigned sessions

        Given path ishPath + '/1001'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Cannot delete faculty assigned to courses."



    Scenario: (-) Delete NOT existing faculty

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."
