@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/timetable'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/timetable'
        * def ishPathClass = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create Timetable by admin

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"700","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["700"])].id
        * print "classId = " + classId
#       <--->

        * def newTimetable =
            """
            [
            {"id":null,"name":"Course4","code":"","room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"start":"2020-02-06T12:00:00.000Z","end":"2020-02-06T13:00:00.000Z"},
            {"id":null,"name":"Course4","code":"","room":"room2","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":2,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes2","privateNotes":"private notes2","payAdjustment":0,"start":"2020-02-07T12:00:00.000Z","end":"2020-02-09T13:00:00.000Z"}
            ]
            """

        Given path ishPath + '/' + classId
        And request newTimetable
        When method POST
        Then status 200

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
            """
            [
            {"id":"#number","temporaryId":null,"name":"Course4","code":null,"room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2020-02-06T12:00:00.000Z","end":"2020-02-06T13:00:00.000Z","publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"hasPaylines":false},
            {"id":"#number","temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2020-02-07T12:00:00.000Z","end":"2020-02-09T13:00:00.000Z","publicNotes":"notes2","privateNotes":"private notes2","payAdjustment":0,"hasPaylines":false}
            ]
            """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Create Timetable by notadmin with access rights

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"701","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["701"])].id
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

        * def newTimetable =
            """
            [
            {"id":null,"name":"Course4","code":"","room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"start":"2020-02-06T12:00:00.000Z","end":"2020-02-06T13:00:00.000Z"},
            {"id":null,"name":"Course4","code":"","room":"room2","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":2,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes2","privateNotes":"private notes2","payAdjustment":0,"start":"2020-02-07T12:00:00.000Z","end":"2020-02-09T13:00:00.000Z"}
            ]
            """

        Given path ishPath + '/' + classId
        And request newTimetable
        When method POST
        Then status 200

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
            """
            [
            {"id":"#number","temporaryId":null,"name":"Course4","code":null,"room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2020-02-06T12:00:00.000Z","end":"2020-02-06T13:00:00.000Z","publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"hasPaylines":false},
            {"id":"#number","temporaryId":null,"name":"Course4","code":null,"room":"room2","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":2,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2020-02-07T12:00:00.000Z","end":"2020-02-09T13:00:00.000Z","publicNotes":"notes2","privateNotes":"private notes2","payAdjustment":0,"hasPaylines":false}
            ]
            """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Update Timetable by admin

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"703","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["703"])].id
        * print "classId = " + classId
#       <--->

        * def newTimetable =
            """
            [
            {"id":null,"name":"Course4","code":"","room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"start":"2020-02-06T12:00:00.000Z","end":"2020-02-06T13:00:00.000Z"},
            {"id":null,"name":"Course4","code":"","room":"room2","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":2,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes2","privateNotes":"private notes2","payAdjustment":0,"start":"2020-02-07T12:00:00.000Z","end":"2020-02-09T13:00:00.000Z"}
            ]
            """

        Given path ishPath + '/' + classId
        And request newTimetable
        When method POST
        Then status 200

#       <---> Update Timetable:
        * def newTimetable =
            """
            [
            {"id":null,"name":"Course4","code":"","room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"start":"2020-02-10T12:00:00.000Z","end":"2020-02-10T13:00:00.000Z"}
            ]
            """

        Given path ishPath + '/' + classId
        And request newTimetable
        When method POST
        Then status 200

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
            """
            [
            {"id":"#number","temporaryId":null,"name":"Course4","code":null,"room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"siteTimezone":"Australia/Sydney","courseClassTutorIds":[],"temporaryTutorIds":[],"start":"2020-02-10T12:00:00.000Z","end":"2020-02-10T13:00:00.000Z","publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"hasPaylines":false}
            ]
            """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Create Timetable by notadmin without access rights

#       <----->  Add a new class and define its id:
        * def newClass = {"id":null,"code":"702","courseId":4,"courseCode":"course4","courseName":null,"endDateTime":null,"startDateTime":null,"attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        Given path ishPathList
        And param entity = 'CourseClass'
        And param columns = 'code'
        When method GET
        Then status 200

        * def classId = get[0] response.rows[?(@.values == ["702"])].id
        * print "classId = " + classId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def newTimetable =
            """
            [
            {"id":null,"name":"Course4","code":"","room":"room1","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":1,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes1","privateNotes":"private notes1","payAdjustment":0,"start":"2020-02-06T12:00:00.000Z","end":"2020-02-06T13:00:00.000Z"},
            {"id":null,"name":"Course4","code":"","room":"room2","site":"site1","tutors":[],"contactIds":[],"courseId":4,"classId":null,"roomId":2,"siteId":201,"courseClassTutorIds":[],"publicNotes":"notes2","privateNotes":"private notes2","payAdjustment":0,"start":"2020-02-07T12:00:00.000Z","end":"2020-02-09T13:00:00.000Z"}
            ]
            """

        Given path ishPath + '/' + classId
        And request newTimetable
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit timetable events. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



#    Scenario: (-) Create Timetable for not existing class
#
#        * def newTimetable =
#            """
#            {
#            "classId":99999,
#            "contactId":5,
#            "roleId":1,
#            "confirmedOn":null,
#            "isInPublicity":false
#            }
#            """
#
#        Given path ishPath
#        And request newTimetable
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Class is wrong"
#
#
#
#    Scenario: (-) Create Timetable for not existing contact
#
#        * def newTimetable =
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
#        And request newTimetable
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Tutor is wrong"
#
#
#
#    Scenario: (-) Create Timetable for with existing role
#
#        * def newTimetable =
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
#        And request newTimetable
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Defined tutor role is wrong"