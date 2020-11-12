@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/course'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all Courses by admin

        Given path ishPathList
        And param entity = 'Course'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4"]



    Scenario: (+) Get Course by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        {
            "id":1,
            "createdOn":"#ignore",
            "modifiedOn":"#ignore",
            "name":"Course1",
            "code":"course1",
            "tags":[],
            "enrolmentType":"Open enrolment",
            "allowWaitingLists":true,
            "dataCollectionRuleId":102,
            "dataCollectionRuleName":"Accredited course",
            "status":"Enabled and visible online",
            "brochureDescription":null,
            "currentClassesCount":1,
            "futureClasseCount":1,
            "selfPacedclassesCount":0,
            "unscheduledClasseCount":0,
            "passedClasseCount":1,
            "cancelledClassesCount":"#number",
            "studentWaitingListCount":0,
            "hasEnrolments":true,
            "webDescription":null,
            "documents":[],
            "relatedlSalables":[],
            "qualificationId":1,
            "qualNationalCode":"UEE30807",
            "qualTitle":"Electrotechnology Electrician",
            "qualLevel":"Certificate III in",
            "isSufficientForQualification":false,
            "isVET":true,
            "fieldOfEducation":"0313",
            "reportableHours":0.0,
            "modules":[],
            "customFields":{},
            "rules":[],
            "isTraineeship":false,
            "currentlyOffered":true
        }
        """



    Scenario: (+) Get Course without Qualification by admin

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course2",
        "code":"course2",
        "tags":[],
        "enrolmentType":"Open enrolment",
        "allowWaitingLists":true,
        "dataCollectionRuleId":102,
        "dataCollectionRuleName":"Accredited course",
        "status":"Enabled and visible online",
        "brochureDescription":null,
        "currentClassesCount":2,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":"#number",
        "studentWaitingListCount":2,
        "hasEnrolments":true,
        "webDescription":null,
        "documents":[],
        "relatedlSalables":[],
        "qualificationId":null,
        "qualNationalCode":null,
        "qualTitle":null,
        "qualLevel":null,
        "isSufficientForQualification":false,
        "isVET":false,
        "fieldOfEducation":null,
        "reportableHours":0.0,
        "modules":[],
        "customFields":{},
        "rules":[],
        "isTraineeship":false,
        "currentlyOffered":true
        }
        """



    Scenario: (-) Get not existing Course

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (-) Get Course without id in path

        Given path ishPath
        When method GET
        Then status 405



    Scenario: (+) Get list of all Courses by notadmin

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
        And param entity = 'Course'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4"]



    Scenario: (+) Get Course by notadmin

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
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course4",
        "code":"course4",
        "tags":[],
        "enrolmentType":"Enrolment by application",
        "allowWaitingLists":true,
        "dataCollectionRuleId":102,
        "dataCollectionRuleName":"Accredited course",
        "status":"Enabled and visible online",
        "brochureDescription":null,
        "currentClassesCount":0,
        "futureClasseCount":"#number",
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":"#number",
        "passedClasseCount":0,
        "cancelledClassesCount":"#number",
        "studentWaitingListCount":0,
        "hasEnrolments":true,
        "webDescription":null,
        "documents":[],
        "relatedlSalables":[],
        "qualificationId":3,
        "qualNationalCode":"10218NAT",
        "qualTitle":"Aboriginal Language/s v2",
        "qualLevel":"Certificate I in",
        "isSufficientForQualification":true,
        "isVET":true,
        "fieldOfEducation":"0915",
        "reportableHours":12.0,
        "modules":[{"creditPoints":null,"expiryDays":null,"fieldOfEducation":null,"id":3,"isCustom":null,"type":"UNIT OF COMPETENCY","isOffered":true,"nationalCode":"AUM1001A","nominalHours":"#number","specialization":null,"title":"Manage personal career goals","createdOn":null,"modifiedOn":null}],
        "customFields":{},
        "rules":[],
        "isTraineeship":false,
        "currentlyOffered":true
        }
        """
