@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass'
        * def ishPathCourse = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create Class by admin

        * def newClass =
        """
        {
        "id":null,
        "code":"100",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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

        * def id = get[0] response.rows[?(@.values == ["100"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id": "#(~~id)",
        "code": "100",
        "courseId": 4,
        "courseCode": "course4",
        "courseName": "Course4",
        "endDateTime": null,
        "startDateTime": null,
        "attendanceType": "Full-time attendance",
        "deliveryMode": "Workplace",
        "fundingSource": "Domestic full fee paying student",
        "budgetedPlaces": 20,
        "censusDate": "2020-02-01",
        "createdOn": "#ignore",
        "modifiedOn": "#ignore",
        "deposit": 0.00,
        "detBookingId": "qwerty",
        "expectedHours": 0.0,
        "feeExcludeGST": 0.00,
        "feeHelpClass": true,
        "finalDetExport": null,
        "fullTimeLoad": null,
        "initialDetExport": null,
        "isActive": true,
        "isCancelled": false,
        "isDistantLearningCourse": false,
        "isShownOnWeb": false,
        "maxStudentAge": 30,
        "maximumDays": null,
        "maximumPlaces": 25,
        "message": "qwerty",
        "midwayDetExport": null,
        "minStudentAge": 10,
        "minimumPlaces": 10,
        "reportingPeriod": 2019,
        "sessionsCount": 0,
        "suppressAvetmissExport": true,
        "vetCourseSiteID": 123,
        "vetFundingSourceStateID": "123",
        "vetPurchasingContractID": "qwerty",
        "vetPurchasingContractScheduleID": "456",
        "webDescription": "qwerty",
        "relatedFundingSourceId": null,
        "qualificationHours": null,
        "nominalHours": "#number",
        "classroomHours": 0,
        "studentContactHours": 0,
        "reportableHours": 12.0,
        "roomId": null,
        "virtualSiteId": null,
        "taxId": 1,
        "summaryFee": null,
        "summaryDiscounts": null,
        "enrolmentsToProfitLeftCount": 0,
        "allEnrolmentsCount": 0,
        "allOutcomesCount": 0,
        "inProgressOutcomesCount": 0,
        "passOutcomesCount": 0,
        "failedOutcomesCount": 0,
        "withdrawnOutcomesCount": 0,
        "otherOutcomesCount": 0,
        "successAndQueuedEnrolmentsCount": 0,
        "canceledEnrolmentsCount": 0,
        "failedEnrolmentsCount": 0,
        "tags": [{"id": 231,"name": "class1","status": null,"system": null,"urlPath": null,"content": null,"color": null,"weight": null,"taggedRecordsCount": null,"childrenCount": null,"created": "#ignore","modified": "#ignore","requirements": [],"childTags": []}],
        "documents": [
        {"shared":true,"thumbnail":null,"access":"Private","added":"#ignore","description":"Private description","createdOn":"#ignore","tags":[],"attachmentRelations":"#ignore","versionId":null,"modifiedOn":"#ignore","removed":false,"attachedRecordsCount":"#ignore","versions":[{"fileName":"defaultPrivateDocument.txt","thumbnail":null,"size":"22 b","added":"#ignore","createdBy":"onCourse Administrator","id":200,"mimeType":"text\/plain","url":null}],"name":"defaultPrivateDocument","id":200}
        ],
        "isTraineeship": false,
        "customFields": {}
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Class to Traineeship course

#       <---> Create Traineeship course:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"post99",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Traineeship Course",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[{"id":3}],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":true,
        "currentlyOffered":false
        }
        """

        Given path ishPathCourse
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def courseId = get[0] response.rows[?(@.values == ["post99"])].id
        * print "courseId = " + courseId

#       <---> Create class for Traineeship course:
        * def newClass =
        """
        {
        "id":null,
        "code":"621",
        "courseId":"#(courseId)",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathList
        And param entity = 'CourseClass'
        And param search = 'code == "621" and course.id == ' + courseId
        And param columns = 'code'
        When method GET
        Then status 200
        * def traineeshipId = response.rows[0].id

        Given path ishPath + '/' + traineeshipId
        When method DELETE
        Then status 204

        Given path ishPathCourse + '/' + courseId
        When method DELETE
        Then status 204



    Scenario: (-) Create Class without Course

        * def newClass =
        """
        {
        "id":null,
        "code":"102",
        "courseId":null,
        "courseCode":"CDHGTR",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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
        Then status 400
        And match $.errorMessage == "You must link this class to a course."



    Scenario: (-) Create Class to not existing Course

        * def newClass =
        """
        {
        "id":null,
        "code":"103",
        "courseId":99999,
        "courseCode":"CDdggd",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (-) Create Class without code

        * def newClass =
        """
        {
        "id":null,
        "code":null,
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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
        Then status 400
        And match $.errorMessage == "You need to enter a class code."



    Scenario: (-) Create Class with existing code

        * def newClass =
        """
        {
        "id":null,
        "code":"1",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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
        Then status 400
        And match $.errorMessage == "The class code must be unique within the course."



    Scenario: (-) Create Class with invalid code

        * def newClass =
        """
        {
        "id":null,
        "code":"sf@#$ hj",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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
        Then status 400
        And match $.errorMessage == "Class code must start and end with letters or numbers and may contain full stops."



    Scenario: (+) Create Class by notadmin with access rights

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

        * def newClass =
        """
        {
        "id":null,
        "code":"101",
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
        "fullTimeLoad":null,
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
        "reportingPeriod":"2019",
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

        * def id = get[0] response.rows[?(@.values == ["101"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id": "#(~~id)",
        "code": "101",
        "courseId": 4,
        "courseCode": "course4",
        "courseName": "Course4",
        "endDateTime": null,
        "startDateTime": null,
        "attendanceType": "Full-time attendance",
        "deliveryMode": "Workplace",
        "fundingSource": "Domestic full fee paying student",
        "budgetedPlaces": 20,
        "censusDate": "2020-02-01",
        "createdOn": "#ignore",
        "modifiedOn": "#ignore",
        "deposit": 0.00,
        "detBookingId": "qwerty",
        "expectedHours": 0.0,
        "feeExcludeGST": 0.00,
        "feeHelpClass": true,
        "finalDetExport": null,
        "fullTimeLoad": null,
        "initialDetExport": null,
        "isActive": true,
        "isCancelled": false,
        "isDistantLearningCourse": false,
        "isShownOnWeb": false,
        "maxStudentAge": 30,
        "maximumDays": null,
        "maximumPlaces": 25,
        "message": "qwerty",
        "midwayDetExport": null,
        "minStudentAge": 10,
        "minimumPlaces": 10,
        "reportingPeriod": 2019,
        "sessionsCount": 0,
        "suppressAvetmissExport": true,
        "vetCourseSiteID": 123,
        "vetFundingSourceStateID": "123",
        "vetPurchasingContractID": "qwerty",
        "vetPurchasingContractScheduleID": "456",
        "webDescription": "qwerty",
        "relatedFundingSourceId": null,
        "qualificationHours": null,
        "nominalHours": "#number",
        "classroomHours": 0,
        "studentContactHours": 0,
        "reportableHours": 12.0,
        "roomId": null,
        "virtualSiteId": null,
        "taxId": 1,
        "summaryFee": null,
        "summaryDiscounts": null,
        "enrolmentsToProfitLeftCount": 0,
        "allEnrolmentsCount": 0,
        "allOutcomesCount": 0,
        "inProgressOutcomesCount": 0,
        "passOutcomesCount": 0,
        "failedOutcomesCount": 0,
        "withdrawnOutcomesCount": 0,
        "otherOutcomesCount": 0,
        "successAndQueuedEnrolmentsCount": 0,
        "canceledEnrolmentsCount": 0,
        "failedEnrolmentsCount": 0,
        "tags": [{"id": 231,"name": "class1","status": null,"system": null,"urlPath": null,"content": null,"color": null,"weight": null,"taggedRecordsCount": null,"childrenCount": null,"created": "#ignore","modified": "#ignore","requirements": [],"childTags": []}],
        "documents":"#ignore",
        "isTraineeship": false,
        "customFields": {}
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new Class by notadmin without access rights

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

        * def newClass = {}

        Given path ishPath
        And request newClass
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create class. Please contact your administrator"
