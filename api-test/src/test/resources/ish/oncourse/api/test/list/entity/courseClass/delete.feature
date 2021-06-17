@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/courseClass'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


        
    Scenario: (+) Delete existing Class by admin

#       <----->  Add a new entity for deleting and get id:
        * def newClass =
        """
        {
        "id":null,
        "code":"200",
        "courseId":4,
        "courseCode":"course4",
        "courseName":null,
        "endDateTime":null,
        "startDateTime":null,
        "attendanceType":"Full-time attendance",
        "deliveryMode":"Workplace",
        "fundingSource":"Domestic full fee paying student",
        "budgetedPlaces":20,
        "censusDate":"2020-02-01",
        "createdOn":null,
        "modifiedOn":null,
        "deposit":null,
        "detBookingId":"qwerty",
        "expectedHours":null,
        "feeExcludeGST":null,
        "feeHelpClass":true,
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "isDistantLearningCourse":false,
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,  
        "sessionsCount":null,
        "suppressAvetmissExport":true,
        "vetCourseSiteID":123,
        "vetFundingSourceStateID":"123",
        "vetPurchasingContractID":"qwerty",
        "vetPurchasingContractScheduleID":"456",
        "webDescription":"qwerty",
        "relatedFundingSourceId":null,
        "roomId":null,
        "taxId":null,
        "summaryFee":null,
        "summaryDiscounts":null,
        "enrolmentsToProfitLeftCount":null,
        "successAndQueuedEnrolmentsCount":null,
        "tags":[{"id":231}],
        "documents":[{"id":200}],
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["200"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing Class by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newClass =
        """
        {
        "id":null,
        "code":"201",
        "courseId":4,
        "courseCode":"course4",
        "courseName":null,
        "endDateTime":null,
        "startDateTime":null,
        "attendanceType":"Full-time attendance",
        "deliveryMode":"Workplace",
        "fundingSource":"Domestic full fee paying student",
        "budgetedPlaces":20,
        "censusDate":"2020-02-01",
        "createdOn":null,
        "modifiedOn":null,
        "deposit":null,
        "detBookingId":"qwerty",
        "expectedHours":null,
        "feeExcludeGST":null,
        "feeHelpClass":true,
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "isDistantLearningCourse":false,
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,        
        "sessionsCount":null,
        "suppressAvetmissExport":true,
        "vetCourseSiteID":123,
        "vetFundingSourceStateID":"123",
        "vetPurchasingContractID":"qwerty",
        "vetPurchasingContractScheduleID":"456",
        "webDescription":"qwerty",
        "relatedFundingSourceId":null,
        "roomId":null,
        "taxId":null,
        "summaryFee":null,
        "summaryDiscounts":null,
        "enrolmentsToProfitLeftCount":null,
        "successAndQueuedEnrolmentsCount":null,
        "tags":[{"id":231}],
        "documents":[{"id":200}],
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["201"])].id
        * print "id = " + id

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete existing Class by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newClass =
        """
        {
        "id":null,
        "code":"202",
        "courseId":4,
        "courseCode":"course4",
        "courseName":null,
        "endDateTime":null,
        "startDateTime":null,
        "attendanceType":"Full-time attendance",
        "deliveryMode":"Workplace",
        "fundingSource":"Domestic full fee paying student",
        "budgetedPlaces":20,
        "censusDate":"2020-02-01",
        "createdOn":null,
        "modifiedOn":null,
        "deposit":null,
        "detBookingId":"qwerty",
        "expectedHours":null,
        "feeExcludeGST":null,
        "feeHelpClass":true,
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "isDistantLearningCourse":false,
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,        
        "sessionsCount":null,
        "suppressAvetmissExport":true,
        "vetCourseSiteID":123,
        "vetFundingSourceStateID":"123",
        "vetPurchasingContractID":"qwerty",
        "vetPurchasingContractScheduleID":"456",
        "webDescription":"qwerty",
        "relatedFundingSourceId":null,
        "roomId":null,
        "taxId":null,
        "summaryFee":null,
        "summaryDiscounts":null,
        "enrolmentsToProfitLeftCount":null,
        "successAndQueuedEnrolmentsCount":null,
        "tags":[{"id":231}],
        "documents":[{"id":200}],
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["202"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete class. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete existing Class with enrolments

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match response.errorMessage == "This class has enrolled students."



    Scenario: (-) Delete NOT existing Class

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

