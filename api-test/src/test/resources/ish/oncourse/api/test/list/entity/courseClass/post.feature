@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass'
        * def ishPathCourse = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



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
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "minimumSessionsToComplete":null,
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "feeHelpClass": false,
        "finalDetExport": null,
        "initialDetExport": null,
        "isActive": true,
        "isCancelled": false,
        "type":"With Sessions",
        "isShownOnWeb": false,
        "maxStudentAge": 30,
        "maximumDays": null,
        "maximumPlaces": 25,
        "message": "qwerty",
        "midwayDetExport": null,
        "minStudentAge": 10,
        "minimumPlaces": 10,
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
        "minimumSessionsToComplete":null,
        "tags": [231],
        "documents": [
        {"shared":true,"thumbnail":null,"access":"Private","added":"#ignore","description":"Private description","createdOn":"#ignore","tags":[],"attachmentRelations":"#ignore","modifiedOn":"#ignore","removed":false,"attachedRecordsCount":"#ignore","versions":[{"fileName":"defaultPrivateDocument.txt","thumbnail":null,"size":"22 b","added":"#ignore","createdBy":"onCourse Administrator","id":200,"mimeType":"text\/plain","url":"#string","content":null, current:true}],"name":"defaultPrivateDocument","id":200,"urlWithoutVersionId":"#string"}
        ],
        "isTraineeship": false,
        "customFields": {},
        "specialTagId":null,
        "portalDocAccessStart":null,
        "portalDocAccessEnd":null
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
        "tags":[227],
        "documents":[{"id":200}],
        "modules":[{"id":3}],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":true,
        "currentlyOffered":false,
        "feeHelpClass":false,
        "specialTagId":null
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
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
        }
        """

        Given path ishPath
        And request newClass
        When method POST
        Then status 400
        And match $.errorMessage == "Class code must start and end with letters or numbers and may contain full stops."



    Scenario: (+) Create Class by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsCreate'}


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
        "finalDetExport":null,
        "initialDetExport":null,
        "isActive":true,
        "isCancelled":false,
        "type":"With Sessions",
        "isShownOnWeb":false,
        "maxStudentAge":"30",
        "maximumDays":null,
        "maximumPlaces":25,
        "message":"qwerty",
        "midwayDetExport":null,
        "minStudentAge":"10",
        "minimumPlaces":10,
        "reportableHours":12,
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
        "minimumSessionsToComplete":null,
        "tags":[231],
        "documents":[{"id":200}],
        "isTraineeship":false,
        "specialTagId":null
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
        "finalDetExport": null,
        "initialDetExport": null,
        "isActive": true,
        "isCancelled": false,
        "type":"With Sessions",
        "isShownOnWeb": false,
        "maxStudentAge": 30,
        "maximumDays": null,
        "maximumPlaces": 25,
        "message": "qwerty",
        "midwayDetExport": null,
        "minStudentAge": 10,
        "minimumPlaces": 10,
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
        "minimumSessionsToComplete":null,
        "tags": [231],
        "documents":"#ignore",
        "isTraineeship": false,
        "customFields": {},
        "feeHelpClass":false,
        "specialTagId":null,
        "portalDocAccessStart":null,
        "portalDocAccessEnd":null
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create new Class by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def newClass = {}

        Given path ishPath
        And request newClass
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create class. Please contact your administrator"
