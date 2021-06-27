@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/note'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/note'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get Note by admin

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 5
        When method GET
        Then status 200
        And match $ ==
        """
        [
            {
            "id":1003,
            "created":"#ignore",
            "modified":"#ignore",
            "message":"Notes: This Course is for testing contacts merging",
            "createdBy":"onCourse Administrator",
            "modifiedBy":null,
            "entityName":"Course",
            "entityId":5
            }
        ]
        """



    Scenario: (+) Get Note by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 5
        When method GET
        Then status 200
        And match $ ==
        """
        [
            {
            "id":1003,
            "created":"#ignore",
            "modified":"#ignore",
            "message":"Notes: This Course is for testing contacts merging",
            "createdBy":"onCourse Administrator",
            "modifiedBy":null,
            "entityName":"Course",
            "entityId":5
            }
        ]
        """



    Scenario: (-) Get not existing Note

        Given path ishPath
        And param entityName = "Course"
        And param entityId = 99999
        When method GET
        Then status 400
        And match $.errorMessage == "Related object doesn't exist"