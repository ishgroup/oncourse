@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/assessment'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/assessment'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Assessments by admin

        Given path ishPathList
        And param entity = 'Assessment'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001"]



    Scenario: (+) Get Assessment by admin

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1000,
        "code":"code1",
        "name":"assessment 1",
        "gradingTypeId":1,
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":[],
        }
        """



    Scenario: (-) Get not existing Assessment

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Get list of all Assessments by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Assessment'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001"]



    Scenario: (+) Get Assessment by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":1001,
        "code":"code2",
        "name":"assessment 2",
        "gradingTypeId":1,
        "tags":[],
        "active":true,
        "description":"some description",
        "documents":
            [{
            "attachedRecordsCount":"#ignore",
            "id":201,
            "name":"defaultPublicDocument",
            "versionId":null,
            "added":"#ignore",
            "tags":[],
            "thumbnail":null,
            "versions":[{"id":201,"added":"#ignore","createdBy":"onCourse Administrator","fileName":"defaultPublicDocument.txt","mimeType":"text/plain","size":"21 b","url":null,"thumbnail":null}],
            "description":"Public description",
            "access":"Public",
            "shared":true,
            "removed": false,
            "attachmentRelations": "#ignore",
            "createdOn":"#ignore",
            "modifiedOn":"#ignore"
            }]
        }
        """

        