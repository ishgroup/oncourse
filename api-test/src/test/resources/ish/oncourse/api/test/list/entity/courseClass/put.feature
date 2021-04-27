@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/courseClass'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update Class by admin

#       <----->  Add a new entity to update and define its id:
        * def newClass =
        """
        {
        "code":"400",
        "courseId":4,
        "courseCode":"course4",
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
        "isShownOnWeb":true,
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
        "tags":[],
        "documents":[],
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

        * def id = get[0] response.rows[?(@.values == ["400"])].id
        * print "id = " + id
#       <--->

        * def classToUpdate =
        """
        {"id":"#(id)","code":"400UPD","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":null,"attendanceType":"Full-time attendance","deliveryMode":"Workplace","fundingSource":"Domestic full fee paying student","budgetedPlaces":20,"censusDate":"2020-02-01","createdOn":"2020-02-05T15:11:20.124Z","modifiedOn":"2020-02-05T15:16:58.343Z","deposit":0,"detBookingId":"qwer12","expectedHours":null,"feeExcludeGST":0,"feeHelpClass":true,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":22,"maximumDays":null,"maximumPlaces":25,"message":"qwerty","midwayDetExport":null,"minStudentAge":15,"minimumPlaces":15,"sessionsCount":0,"suppressAvetmissExport":true,"vetCourseSiteID":123,"vetFundingSourceStateID":"123","vetPurchasingContractID":"qw","vetPurchasingContractScheduleID":"456","webDescription":"qwertyUPD","relatedFundingSourceId":null,"qualificationHours":null,"nominalHours":12,"classroomHours":0,"studentContactHours":0,"reportableHours":15,"roomId":null,"virtualSiteId":null,"taxId":1,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":0,"allEnrolmentsCount":0,"allOutcomesCount":0,"inProgressOutcomesCount":0,"passOutcomesCount":0,"failedOutcomesCount":0,"withdrawnOutcomesCount":0,"otherOutcomesCount":0,"successAndQueuedEnrolmentsCount":0,"canceledEnrolmentsCount":0,"failedEnrolmentsCount":0,"tags":[{"id":231}],"documents":[{"id":200}],"isTraineeship":false}
        """

        Given path ishPath + '/' + id
        And request classToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.modifiedOn != classToUpdate.modifiedOn

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Class by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newClass =
        """
        {
        "code":"401",
        "courseId":4,
        "courseCode":"course4",
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
        "isShownOnWeb":true,
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

        * def id = get[0] response.rows[?(@.values == ["401"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def classToUpdate =
        """
        {"id":"#(id)","code":"401UPD","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":null,"attendanceType":"Full-time attendance","deliveryMode":"Workplace","fundingSource":"Domestic full fee paying student","budgetedPlaces":20,"censusDate":"2020-02-01","createdOn":"2020-02-05T15:11:20.124Z","modifiedOn":"2020-02-05T15:16:58.343Z","deposit":0,"detBookingId":"qwer12","expectedHours":0.0,"feeExcludeGST":0,"feeHelpClass":true,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":22,"maximumDays":null,"maximumPlaces":25,"message":"qwerty","midwayDetExport":null,"minStudentAge":15,"minimumPlaces":15,"sessionsCount":0,"suppressAvetmissExport":true,"vetCourseSiteID":123,"vetFundingSourceStateID":"123","vetPurchasingContractID":"qw","vetPurchasingContractScheduleID":"456","webDescription":"qwertyUPD","relatedFundingSourceId":null,"qualificationHours":null,"nominalHours":12,"classroomHours":0,"studentContactHours":0,"reportableHours":15,"roomId":null,"virtualSiteId":null,"taxId":1,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":0,"allEnrolmentsCount":0,"allOutcomesCount":0,"inProgressOutcomesCount":0,"passOutcomesCount":0,"failedOutcomesCount":0,"withdrawnOutcomesCount":0,"otherOutcomesCount":0,"successAndQueuedEnrolmentsCount":0,"canceledEnrolmentsCount":0,"failedEnrolmentsCount":0,"tags":[],"documents":[],"isTraineeship":false}
        """

        Given path ishPath + '/' + id
        And request classToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {"id":"#(~~id)","code":"401UPD","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":null,"attendanceType":"Full-time attendance","deliveryMode":"Workplace","fundingSource":"Domestic full fee paying student","budgetedPlaces":20,"censusDate":"2020-02-01","createdOn":"#ignore","modifiedOn":"#ignore","deposit":0.00,"detBookingId":"qwer12","expectedHours":0.0,"feeExcludeGST":0.00,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":22,"maximumDays":null,"maximumPlaces":25,"message":"qwerty","midwayDetExport":null,"minStudentAge":15,"minimumPlaces":15,"sessionsCount":0,"suppressAvetmissExport":true,"vetCourseSiteID":123,"vetFundingSourceStateID":"123","vetPurchasingContractID":"qw","vetPurchasingContractScheduleID":"456","webDescription":"qwertyUPD","relatedFundingSourceId":null,"qualificationHours":null,"nominalHours":#number,"classroomHours":0,"studentContactHours":0,"reportableHours":15.0,"roomId":null,"virtualSiteId":null,"taxId":1,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":0,"allEnrolmentsCount":0,"allOutcomesCount":0,"inProgressOutcomesCount":0,"passOutcomesCount":0,"failedOutcomesCount":0,"withdrawnOutcomesCount":0,"otherOutcomesCount":0,"successAndQueuedEnrolmentsCount":0,"canceledEnrolmentsCount":0,"failedEnrolmentsCount":0,"tags":[],"documents":[],"isTraineeship":false,"customFields":{}}
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Class by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsPrint', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def classToUpdate = {}

        Given path ishPath + '/5'
        And request classToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit class. Please contact your administrator"



    Scenario: (-) Update Class to empty Code

#       <----->  Add a new entity to update and define its id:
        * def newClass =
        """
        {
        "code":"402",
        "courseId":4,
        "courseCode":"course4",
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
        "tags":[],
        "documents":[],
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

        * def id = get[0] response.rows[?(@.values == ["402"])].id
        * print "id = " + id
#       <--->

        * def classToUpdate =
        """
        {"id":"#(id)","code":null,"courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":null,"attendanceType":"Full-time attendance","deliveryMode":"Workplace","fundingSource":"Domestic full fee paying student","budgetedPlaces":20,"censusDate":"2020-02-01","createdOn":"2020-02-05T15:11:20.124Z","modifiedOn":"2020-02-05T15:16:58.343Z","deposit":0,"detBookingId":"qwer12","expectedHours":null,"feeExcludeGST":0,"feeHelpClass":true,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":22,"maximumDays":null,"maximumPlaces":25,"message":"qwerty","midwayDetExport":null,"minStudentAge":15,"minimumPlaces":15,"sessionsCount":0,"suppressAvetmissExport":true,"vetCourseSiteID":123,"vetFundingSourceStateID":"123","vetPurchasingContractID":"qw","vetPurchasingContractScheduleID":"456","webDescription":"qwertyUPD","relatedFundingSourceId":null,"qualificationHours":null,"nominalHours":12,"classroomHours":0,"studentContactHours":0,"reportableHours":15,"roomId":null,"virtualSiteId":null,"taxId":1,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":0,"allEnrolmentsCount":0,"allOutcomesCount":0,"inProgressOutcomesCount":0,"passOutcomesCount":0,"failedOutcomesCount":0,"withdrawnOutcomesCount":0,"otherOutcomesCount":0,"successAndQueuedEnrolmentsCount":0,"canceledEnrolmentsCount":0,"failedEnrolmentsCount":0,"tags":[{"id":231}],"documents":[{"id":200}],"isTraineeship":false}
        """

        Given path ishPath + '/' + id
        And request classToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "You need to enter a class code."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Class to existing Code

#       <----->  Add a new entity to update and define its id:
        * def newClass =
        """
        {
        "code":"403",
        "courseId":4,
        "courseCode":"course4",
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
        "tags":[],
        "documents":[],
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

        * def id = get[0] response.rows[?(@.values == ["403"])].id
        * print "id = " + id
#       <--->

        * def classToUpdate =
        """
        {"id":"#(id)","code":"1","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":null,"attendanceType":"Full-time attendance","deliveryMode":"Workplace","fundingSource":"Domestic full fee paying student","budgetedPlaces":20,"censusDate":"2020-02-01","createdOn":"2020-02-05T15:11:20.124Z","modifiedOn":"2020-02-05T15:16:58.343Z","deposit":0,"detBookingId":"qwer12","expectedHours":null,"feeExcludeGST":0,"feeHelpClass":true,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":22,"maximumDays":null,"maximumPlaces":25,"message":"qwerty","midwayDetExport":null,"minStudentAge":15,"minimumPlaces":15,"sessionsCount":0,"suppressAvetmissExport":true,"vetCourseSiteID":123,"vetFundingSourceStateID":"123","vetPurchasingContractID":"qw","vetPurchasingContractScheduleID":"456","webDescription":"qwertyUPD","relatedFundingSourceId":null,"qualificationHours":null,"nominalHours":12,"classroomHours":0,"studentContactHours":0,"reportableHours":15,"roomId":null,"virtualSiteId":null,"taxId":1,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":0,"allEnrolmentsCount":0,"allOutcomesCount":0,"inProgressOutcomesCount":0,"passOutcomesCount":0,"failedOutcomesCount":0,"withdrawnOutcomesCount":0,"otherOutcomesCount":0,"successAndQueuedEnrolmentsCount":0,"canceledEnrolmentsCount":0,"failedEnrolmentsCount":0,"tags":[{"id":231}],"documents":[{"id":200}],"isTraineeship":false}
        """

        Given path ishPath + '/' + id
        And request classToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The class code must be unique within the course."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204


        
    Scenario: (-) Update not existing Class

        * def classToUpdate =
        """
        {"id":99999,"code":"99999","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":null,"attendanceType":"Full-time attendance","deliveryMode":"Workplace","fundingSource":"Domestic full fee paying student","budgetedPlaces":20,"censusDate":"2020-02-01","createdOn":"2020-02-05T15:11:20.124Z","modifiedOn":"2020-02-05T15:16:58.343Z","deposit":0,"detBookingId":"qwer12","expectedHours":null,"feeExcludeGST":0,"feeHelpClass":true,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":22,"maximumDays":null,"maximumPlaces":25,"message":"qwerty","midwayDetExport":null,"minStudentAge":15,"minimumPlaces":15,"sessionsCount":0,"suppressAvetmissExport":true,"vetCourseSiteID":123,"vetFundingSourceStateID":"123","vetPurchasingContractID":"qw","vetPurchasingContractScheduleID":"456","webDescription":"qwertyUPD","relatedFundingSourceId":null,"qualificationHours":null,"nominalHours":12,"classroomHours":0,"studentContactHours":0,"reportableHours":15,"roomId":null,"virtualSiteId":null,"taxId":1,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":0,"allEnrolmentsCount":0,"allOutcomesCount":0,"inProgressOutcomesCount":0,"passOutcomesCount":0,"failedOutcomesCount":0,"withdrawnOutcomesCount":0,"otherOutcomesCount":0,"successAndQueuedEnrolmentsCount":0,"canceledEnrolmentsCount":0,"failedEnrolmentsCount":0,"tags":[{"id":231}],"documents":[{"id":200}],"isTraineeship":false}
        """

        Given path ishPath + '/99999'
        And request classToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
