@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/assessment'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/assessment'
        * def ishPathClass = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create Assessment by admin

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"720","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["720"])].id
        * print "classId = " + classId
#       <--->

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

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ == [{"id":"#number","assessmentId":1000,"courseClassId":"#(~~classId)","assessmentCode":"code1","assessmentName":"assessment 1","contactIds":[],"moduleIds":[],"releaseDate":"2019-01-01T00:00:00.000Z","dueDate":"2020-02-01T03:10:00.000Z"}]

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Create Assessment by notadmin with access rights

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"721","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["721"])].id
        * print "classId = " + classId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

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

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ == [{"id":"#number","assessmentId":1000,"courseClassId":"#(~~classId)","assessmentCode":"code1","assessmentName":"assessment 1","contactIds":[],"moduleIds":[],"releaseDate":"2019-01-01T00:00:00.000Z","dueDate":"2020-02-01T03:10:00.000Z"}]

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Create Assessment by notadmin without access rights

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"722","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["722"])].id
        * print "classId = " + classId

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
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create Assessment Class. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Create Assessment for not existing class

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
            "courseClassId":99999
            }
            """

        Given path ishPath
        And request newAssessment
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



#    Scenario: (-) Create Assessment for not existing contact
#
#        * def newAssessment =
#            """
#            {
#            "classId":6,
#            "contactId":99999,
#            "roleId":1,
#            "confirmedOn":null,
#            "isInPublicity":false
#            }
#            """
#
#        Given path ishPath
#        And request newAssessment
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Assessment is wrong"
#
#
#
#    Scenario: (-) Create Assessment with not existing role
#
#        * def newAssessment =
#            """
#            {
#            "classId":6,
#            "contactId":5,
#            "roleId":99999,
#            "confirmedOn":null,
#            "isInPublicity":false
#            }
#            """
#
#        Given path ishPath
#        And request newAssessment
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Defined tutor role is wrong"



#    Scenario: (-) Create anoter the same Assessment
#
#        * def newAssessment =
#            """
#            {}
#            """
#
#        Given path ishPath
#        And request newAssessment
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Defined tutor role is wrong"