@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/faculty'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/faculty'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'




    Scenario: (+) Get list of all faculties by admin

        Given path ishPathList
        And param entity = 'Faculty'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["testFacultyName1","testFacultyName2","testFacultyName3"]



    Scenario: (+) Get list of all faculties by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}


#       <--->

        Given path ishPathList
        And param entity = 'Faculty'
        When method GET
        Then status 200
        And match $.rows[*].values[*] contains ["testFacultyName1","testFacultyName2","testFacultyName3"]



    Scenario: (+) Get faculty by admin

        Given path ishPath + "/1001"
        When method GET
        Then status 200

        And match $ contains
            """
            {
              "id": 1001,
              "name": "testFacultyName1",
              "code": "testFacultyCode1",
              "webDescription": "testWebDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "createdOn": "#ignore",
              "modifiedOn": "#ignore",
              "tags": [],
              "documents": [],
              "relatedCourses": [{"relatedSellables":[],"code":"lead1","qualificationId":null,"documents":[],"isTraineeship":null,"customFields":{},"hasEnrolments":null,"rules":[],"fieldOfEducation":null,"createdOn":null,"modifiedOn":null,"studentWaitingListCount":null,"reportableHours":null,"dataCollectionRuleId":null,"id":112,"selfPacedclassesCount":null,"webDescription":null,"shortWebDescription":null,"feeHelpClass":null,"passedClasseCount":null,"isVET":null,"cancelledClassesCount":null,"hybridClassesCount":null,"currentClassesCount":null,"qualNationalCode":null,"allowWaitingLists":null,"modules":[],"fullTimeLoad":null,"tags":[],"qualLevel":null,"facultyId":null,"enrolmentType":null,"isSufficientForQualification":null,"brochureDescription":null,"currentlyOffered":null,"name":"FirstLeadCourse","dataCollectionRuleName":null,"futureClasseCount":null,"unscheduledClasseCount":null,"qualTitle":null,"status":null, "specialTagId":null, "attainmentText":null}]
            }
            """



    Scenario: (+) Get faculty by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}


#       <--->

        Given path ishPath + "/1001"
        When method GET
        Then status 200

        And match $ contains
            """
            {
              "id": 1001,
              "name": "testFacultyName1",
              "code": "testFacultyCode1",
              "webDescription": "testWebDescription1",
              "shortWebDescription": "shortWebDescription1",
              "isShownOnWeb": true,
              "createdOn": "#ignore",
              "modifiedOn": "#ignore",
              "tags": [],
              "documents": [],
              "relatedCourses": [{"relatedSellables":[],"code":"lead1","qualificationId":null,"documents":[],"isTraineeship":null,"customFields":{},"hasEnrolments":null,"rules":[],"fieldOfEducation":null,"createdOn":null,"modifiedOn":null,"studentWaitingListCount":null,"reportableHours":null,"dataCollectionRuleId":null,"id":112,"selfPacedclassesCount":null,"webDescription":null,"shortWebDescription":null,"feeHelpClass":null,"passedClasseCount":null,"isVET":null,"cancelledClassesCount":null,"hybridClassesCount":null,"currentClassesCount":null,"qualNationalCode":null,"allowWaitingLists":null,"modules":[],"fullTimeLoad":null,"tags":[],"qualLevel":null,"facultyId":null,"enrolmentType":null,"isSufficientForQualification":null,"brochureDescription":null,"currentlyOffered":null,"name":"FirstLeadCourse","dataCollectionRuleName":null,"futureClasseCount":null,"unscheduledClasseCount":null,"qualTitle":null,"status":null, "specialTagId":null, "attainmentText":null}]
            }
            """



    Scenario: (-) Get not existing faculty

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
