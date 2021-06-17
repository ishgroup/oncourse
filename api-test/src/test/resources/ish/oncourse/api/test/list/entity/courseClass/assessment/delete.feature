@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/courseClass/assessment'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/assessment'
        * def ishPathClass = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Delete existing Assessment by admin

#       <----->  Add a new entity for deleting and get id:
        * def newClass = {"id":null,"code":"900","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["900"])].id
        * print "classId = " + classId

        * def newAssessment =
            """
            {
            "id":null,
            "assessmentId":1000,
            "assessmentCode":"code1",
            "assessmentName":"assessment 1",
            "contactIds":[],
            "moduleIds":[],
            "releaseDate":"2019-01-01T00:00:00.000Z",
            "dueDate":"2020-02-01T03:10:00.000Z",
            "courseClassId":"#(~~classId)"
            }
            """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def assessmentId = get[0] response[*].id
        * print "assessmentId = " + assessmentId
#       <--->

        Given path ishPath + '/' + assessmentId
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ == []

#       <--->  Scenario have been finished. Now remove created class from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Delete existing Assessment by admin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newClass = {"id":null,"code":"901","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["901"])].id
        * print "classId = " + classId

        * def newAssessment =
            """
            {
            "id":null,
            "assessmentId":1000,
            "assessmentCode":"code1",
            "assessmentName":"assessment 1",
            "contactIds":[],
            "moduleIds":[],
            "releaseDate":"2019-01-01T00:00:00.000Z",
            "dueDate":"2020-02-01T03:10:00.000Z",
            "courseClassId":"#(~~classId)"
            }
            """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def assessmentId = get[0] response[*].id
        * print "assessmentId = " + assessmentId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/' + assessmentId
        When method DELETE
        Then status 204

#       <---> Verification of deleting:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ == []

#       <--->  Scenario have been finished. Now remove created class from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Delete existing Assessment by admin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newClass = {"id":null,"code":"902","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["902"])].id
        * print "classId = " + classId

        * def newAssessment =
            """
            {
            "id":null,
            "assessmentId":1000,
            "assessmentCode":"code1",
            "assessmentName":"assessment 1",
            "contactIds":[],
            "moduleIds":[],
            "releaseDate":"2019-01-01T00:00:00.000Z",
            "dueDate":"2020-02-01T03:10:00.000Z",
            "courseClassId":"#(~~classId)"
            }
            """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def assessmentId = get[0] response[*].id
        * print "assessmentId = " + assessmentId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/' + assessmentId
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete Assessment Class. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created class from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Delete not existing Assessment

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."