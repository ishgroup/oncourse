@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/course'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update Course by admin

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put1",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating1",
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
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put1"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":false,
        "code":"UPD1",
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"UPD1",
        "enrolmentType":"Enrolment by application",
        "status":"Enabled and visible online",
        "reportableHours":33,
        "webDescription":"some web description UPD",
        "customFields":{},
        "tags":[{"id":228}],
        "documents":[{"id":201}],
        "relatedSellables":[],
        "modules":[{"id":4}],
        "dataCollectionRuleId":"102",
        "brochureDescription":"some description UPD",
        "qualificationId":4,
        "isTraineeship":false,
        "rules":[{"id":null,"description":"test","startDate":"2020-02-01","endDate":"2020-02-29","repeatEnd":"onDate","repeat":"hour","repeatEndAfter":0,"repeatOn":"2020-02-29","startDateTime":null,"endDateTime":null}]
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"UPD1",
        "code":"UPD1",
        "tags":[{"id":228,"name":"course2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "enrolmentType":"Enrolment by application",
        "allowWaitingLists":false,
        "dataCollectionRuleId":102,
        "dataCollectionRuleName":"Accredited course",
        "status":"Enabled and visible online",
        "brochureDescription":"some description UPD",
        "currentClassesCount":0,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":0,
        "studentWaitingListCount":0,
        "hasEnrolments":false,
        "webDescription":"some web description UPD",
        "documents": "#ignore",
        "relatedSellables":[],
        "qualificationId":4,
        "qualNationalCode":"90946NSW",
        "qualTitle":"Building Studies, Technology",
        "qualLevel":"Advanced Diploma of",
        "isSufficientForQualification":false,
        "isVET":true,
        "fieldOfEducation":"0403",
        "reportableHours":33.0,
        "modules":[{"creditPoints":null,"expiryDays":null,"fieldOfEducation":null,"id":4,"isCustom":null,"type":"UNIT OF COMPETENCY","isOffered":true,"nationalCode":"AUM1002A","nominalHours":32.0,"specialization":null,"title":"Select and use tools and equipment in an automotive manufacturing environment","createdOn":"#ignore","modifiedOn":"#ignore"}],
        "customFields":{},
        "rules":[{"id":"#ignore","description":"test","startDate":"2020-02-01","endDate":"2020-02-29","startDateTime":null,"endDateTime":null,"repeat":"hour","repeatEnd":"onDate","repeatEndAfter":null,"repeatOn":"2020-02-29","created":"#ignore","modified":"#ignore"}],
        "isTraineeship":false,
        "currentlyOffered":true
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course to empty Name

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put4",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating4",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put4"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":true,
        "code":"put4",
        "fieldOfEducation":null,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Name is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course to empty Code

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put5",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating5",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put5"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":true,
        "code":"",
        "fieldOfEducation":null,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating5",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course to existing Code

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put6",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating6",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put6"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":true,
        "code":"course1",
        "fieldOfEducation":null,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating6",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Code must be unique."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course to empty dataCollectionRuleId

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put7",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating7",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put7"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":true,
        "code":"put7",
        "fieldOfEducation":null,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating7",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":null,
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Data collection rule is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Course

        * def courseToUpdate =
        """
        {
        "id":99999,
        "allowWaitingLists":true,
        "code":"put8",
        "fieldOfEducation":null,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating8",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":false
        }
        """

        Given path ishPath + '/99999'
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (-) Update Course module when there are enrolments

        Given path ishPath + '/4'
        When method GET
        Then status 200

        * def courseToUpdate =
        """
        {
        "id":4,
        "name":"Course4",
        "code":"course4",
        "tags":[],
        "enrolmentType":"Enrolment by application",
        "allowWaitingLists":true,
        "dataCollectionRuleId":102,
        "dataCollectionRuleName":"Accredited course",
        "status":"Enabled and visible online",
        "brochureDescription":null,
        "webDescription":null,
        "documents":[],
        "relatedSellables":[],
        "qualificationId":3,
        "qualNationalCode":"10218NAT",
        "qualTitle":"Aboriginal Language/s v2",
        "qualLevel":"Certificate I in",
        "isSufficientForQualification":true,
        "isVET":true,
        "fieldOfEducation":null,
        "reportableHours":15.0,
        "modules":[{"id":4}],
        "customFields":{},
        "rules":[],
        "isTraineeship":false
        }
        """

        Given path ishPath + '/4'
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "There are enrolments in this course. Modifying the modules is not allowed."



    Scenario: (-) Update Course fieldOfEducation to wrong value

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"put6",
        "fieldOfEducation":"123456",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put6"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":false,
        "code":"put6",
        "fieldOfEducation":"1234",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "isTraineeship":false
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "The field of education must be empty or 6 characters long."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course isTraineeship field to true

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"put6t",
        "fieldOfEducation":"123456",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6t",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "isTraineeship":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put6t"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate = {"id":"#(id)","name":"Course updating6t","code":"put6t","tags":[],"enrolmentType":"Open enrolment","allowWaitingLists":true,"dataCollectionRuleId":102,"dataCollectionRuleName":"Accredited course","status":"Enabled","brochureDescription":null,"currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0,"studentWaitingListCount":0,"hasEnrolments":false,"webDescription":null,"documents":[],"relatedSellables":[],"qualificationId":null,"qualNationalCode":null,"qualTitle":null,"qualLevel":null,"isSufficientForQualification":false,"isVET":true,"fieldOfEducation":"","reportableHours":0,"modules":[],"customFields":{},"rules":[],"qualificationId":3,"isTraineeship":true,"currentlyOffered":true}

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Existed course type can not be changed"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course isTraineeship field to false

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"put6tt",
        "fieldOfEducation":"",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6tt",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "qualificationId":3,
        "dataCollectionRuleId":"102",
        "isTraineeship":true,
        "currentlyOffered":true
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put6tt"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":false,
        "code":"put6tt",
        "fieldOfEducation":"123456",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6tt",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "isTraineeship":false
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Existed course type can not be changed"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course isTraineeship field to null

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"put6ttt",
        "fieldOfEducation":"",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6ttt",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "qualificationId":3,
        "dataCollectionRuleId":"102",
        "isTraineeship":true,
        "currentlyOffered":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put6ttt"])].id
        * print "id = " + id
#       <--->

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":false,
        "code":"put6ttt",
        "fieldOfEducation":"123456",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course updating6ttt",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "isTraineeship":null
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Course/Traineeship flag is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Course by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put2",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating2",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":true,
        "currentlyOffered":true
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put2"])].id
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

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":false,
        "code":"UPD2",
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"UPD2",
        "enrolmentType":"Enrolment by application",
        "status":"Enabled",
        "reportableHours":33,
        "webDescription":"some web description UPD",
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "brochureDescription":"some description UPD",
        "qualificationId":4,
        "isTraineeship":true,
        "currentlyOffered":true
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"UPD2",
        "code":"UPD2",
        "tags":[],
        "enrolmentType":"Enrolment by application",
        "allowWaitingLists":false,
        "dataCollectionRuleId":102,
        "dataCollectionRuleName":"Accredited course",
        "status":"Enabled",
        "brochureDescription":"some description UPD",
        "currentClassesCount":0,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":0,
        "studentWaitingListCount":0,
        "hasEnrolments":false,
        "webDescription":"some web description UPD",
        "documents":[],
        "relatedSellables":[],
        "qualificationId":4,
        "qualNationalCode":"90946NSW",
        "qualTitle":"Building Studies, Technology",
        "qualLevel":"Advanced Diploma of",
        "isSufficientForQualification":false,
        "isVET":true,
        "fieldOfEducation":"0403",
        "reportableHours":33.0,
        "modules":[],
        "customFields":{},
        "rules":[],
        "isTraineeship":true,
        "currentlyOffered":true
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Update Course by notadmin without VET access rights

#       <----->  Add a new entity to update and define its id:
        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"put2a",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course for updating2",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "relatedSellables":[{"entityToId":1001, "type":"Product", "relationId":-1}],
        "modules":[],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":true,
        "currentlyOffered":true
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put2a"])].id
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

        * def courseToUpdate =
        """
        {
        "id":"#(id)",
        "allowWaitingLists":false,
        "code":"UPD2",
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"UPD2",
        "enrolmentType":"Enrolment by application",
        "status":"Enabled",
        "reportableHours":33,
        "webDescription":"some web description UPD",
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "brochureDescription":"some description UPD",
        "qualificationId":4,
        "isTraineeship":true,
        "currentlyOffered":true
        }
        """

        Given path ishPath + '/' + id
        And request courseToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit VET details. Please contact your administrator."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Course by notadmin without access rights

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

        * def courseToUpdate = {}

        Given path ishPath + '/5'
        And request courseToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit course. Please contact your administrator"