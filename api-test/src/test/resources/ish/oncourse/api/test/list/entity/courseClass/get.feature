@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all CourseClasses by admin

        Given path ishPathList
        And param entity = 'CourseClass'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8","9","10"]



    Scenario: (+) Get CourseClass by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1,
        "code":"1",
        "courseId":1,
        "courseCode":"course1",
        "courseName":"Course1",
        "endDateTime":"2017-05-09T15:54:31.000Z",
        "startDateTime":"2017-04-30T14:54:31.000Z",
        "attendanceType":"No information",
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "budgetedPlaces":0,
        "censusDate":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "deposit":454.55,
        "detBookingId":null,
        "expectedHours":0.0,
        "feeExcludeGST":454.55,
        "feeHelpClass":false,
        "finalDetExport":null,
        "fullTimeLoad":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "isDistantLearningCourse":false,
        "isShownOnWeb":false,
        "maxStudentAge":null,
        "maximumDays":null,
        "maximumPlaces":999,
        "message":null,
        "midwayDetExport":null,
        "minStudentAge":null,
        "minimumPlaces":1,
        "sessionsCount":10,
        "suppressAvetmissExport":false,
        "vetCourseSiteID":null,
        "vetFundingSourceStateID":null,
        "vetPurchasingContractID":null,
        "vetPurchasingContractScheduleID":null,
        "webDescription":"some web description",
        "relatedFundingSourceId":null,
        "qualificationHours":null,
        "nominalHours":10.00,
        "classroomHours":10.00,
        "studentContactHours":36.0,
        "reportableHours":0.0,
        "roomId":1,
        "virtualSiteId":null,
        "taxId":1,
        "summaryFee":null,
        "summaryDiscounts":null,
        "enrolmentsToProfitLeftCount":0,
        "allEnrolmentsCount":3,
        "allOutcomesCount":3,
        "inProgressOutcomesCount":#number,
        "passOutcomesCount":#number,
        "failedOutcomesCount":#number,
        "withdrawnOutcomesCount":0,
        "otherOutcomesCount":0,
        "successAndQueuedEnrolmentsCount":#number,
        "canceledEnrolmentsCount":0,
        "failedEnrolmentsCount":0,
        "tags":[{"id":230,"name":"class2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":"#ignore","modified":"#ignore","requirements":[],"childTags":[]}],
        "documents":"#ignore",
        "isTraineeship":false,
        "customFields":{}
        }
        """


        

    Scenario: (-) Get not existing CourseClass

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Get list of all CourseClasses by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPathList
        And param entity = 'CourseClass'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","7","8","9","10"]



    Scenario: (+) Get CourseClass by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/4'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":4,
        "code":"1",
        "courseId":2,
        "courseCode":"course2",
        "courseName":"Course2",
        "endDateTime":"2022-12-01T11:14:32.000Z",
        "startDateTime":"2018-12-01T10:14:32.000Z",
        "attendanceType":"No information",
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "budgetedPlaces":0,
        "censusDate":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "deposit":318.18,
        "detBookingId":null,
        "expectedHours":0.0,
        "feeExcludeGST":318.18,
        "feeHelpClass":false,
        "finalDetExport":null,
        "fullTimeLoad":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "isDistantLearningCourse":false,
        "isShownOnWeb":true,
        "maxStudentAge":null,
        "maximumDays":null,
        "maximumPlaces":999,
        "message":null,
        "midwayDetExport":null,
        "minStudentAge":null,
        "minimumPlaces":1,        
        "sessionsCount":5,
        "suppressAvetmissExport":false,
        "vetCourseSiteID":null,
        "vetFundingSourceStateID":null,
        "vetPurchasingContractID":null,
        "vetPurchasingContractScheduleID":null,
        "webDescription":null,
        "relatedFundingSourceId":null,
        "qualificationHours":null,
        "nominalHours":5.00,
        "classroomHours":5.00,
        "studentContactHours":12.0,
        "reportableHours":0.0,
        "roomId":1,
        "virtualSiteId":null,
        "taxId":1,
        "summaryFee":null,
        "summaryDiscounts":null,
        "enrolmentsToProfitLeftCount":0,
        "allEnrolmentsCount":1,
        "allOutcomesCount":1,
        "inProgressOutcomesCount":1,
        "passOutcomesCount":0,
        "failedOutcomesCount":0,
        "withdrawnOutcomesCount":0,
        "otherOutcomesCount":0,
        "successAndQueuedEnrolmentsCount":1,
        "canceledEnrolmentsCount":0,
        "failedEnrolmentsCount":0,
        "tags":[],
        "documents":[],
        "isTraineeship":false,
        "customFields":{}
        }
        """
