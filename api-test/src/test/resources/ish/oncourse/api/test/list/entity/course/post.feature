@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/course'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create Course by admin

        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"post1",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course creating1",
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
        "isTraineeship":false,
        "feeHelpClass":false,
        "rules":[{"id":null,"description":"test","repeatEnd":"after","repeat":"day","repeatEndAfter":"3","startDateTime":"2020-02-11T08:00:00.000Z","endDateTime":"2020-02-12T08:00:00.000Z"}]
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

        * def id = get[0] response.rows[?(@.values == ["post1"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course creating1",
        "code":"post1",
        "tags":[{"id":227,"name":"course1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "enrolmentType":"Open enrolment",
        "allowWaitingLists":true,
        "dataCollectionRuleId":101,
        "dataCollectionRuleName":"Non-accredited course",
        "status":"Enabled",
        "brochureDescription":"some description",
        "currentClassesCount":0,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":0,
        "studentWaitingListCount":0,
        "hasEnrolments":false,
        "webDescription":"some web description",
        "documents":[{"attachedRecordsCount":"#ignore", "id":200,"name":"defaultPrivateDocument","versionId":null,"added":"#ignore","tags":[],"thumbnail":null,"versions":[{"id":200,"added":"#ignore","createdBy":"onCourse Administrator","fileName":"defaultPrivateDocument.txt","mimeType":"text/plain","size":"22 b","url":null,"thumbnail":null}],"description":"Private description","access":"Private","shared":true,"createdOn":"#ignore","modifiedOn":"#ignore","removed":false,"attachmentRelations":"#ignore"}],
        "relatedSellables":"#ignore",
        "qualificationId":3,
        "qualNationalCode":"10218NAT",
        "qualTitle":"Aboriginal Language/s v2",
        "qualLevel":"Certificate I in",
        "isSufficientForQualification":true,
        "isVET":true,
        "fieldOfEducation":"0915",
        "reportableHours":20.0,
        "modules":[{"creditPoints":null,"expiryDays":null,"fieldOfEducation":null,"id":3,"isCustom":null,"type":"UNIT OF COMPETENCY","isOffered":true,"nationalCode":"AUM1001A","nominalHours":"#number","specialization":null,"title":"Manage personal career goals","createdOn":"#ignore","modifiedOn":"#ignore"}],
        "customFields":{},
        "rules":[{"id":"#ignore","description":"test","startDate":null,"endDate":null,"startDateTime":"2020-02-11T08:00:00.000Z","endDateTime":"2020-02-12T08:00:00.000Z","repeat":"day","repeatEnd":"after","repeatEndAfter":3,"repeatOn":null,"created":"#ignore","modified":"#ignore"}],
        "isTraineeship":false,
        "currentlyOffered":true
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Traineeship Course by admin

        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"post1t",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course creating1t",
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
        "currentlyOffered":false,
        "feeHelpClass":false
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

        * def id = get[0] response.rows[?(@.values == ["post1t"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course creating1t",
        "code":"post1t",
        "tags":[{"id":227,"name":"course1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "enrolmentType":"Open enrolment",
        "allowWaitingLists":true,
        "dataCollectionRuleId":101,
        "dataCollectionRuleName":"Non-accredited course",
        "status":"Course disabled",
        "brochureDescription":"some description",
        "currentClassesCount":0,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":0,
        "studentWaitingListCount":0,
        "hasEnrolments":false,
        "webDescription":"some web description",
        "documents":[{"attachedRecordsCount":"#ignore", "id":200,"name":"defaultPrivateDocument","versionId":null,"added":"#ignore","tags":[],"thumbnail":null,"versions":[{"id":200,"added":"#ignore","createdBy":"onCourse Administrator","fileName":"defaultPrivateDocument.txt","mimeType":"text/plain","size":"22 b","url":null,"thumbnail":null}],"description":"Private description","access":"Private","shared":true,"createdOn":"#ignore","modifiedOn":"#ignore","removed":false,"attachmentRelations":"#ignore"}],
        "relatedSellables":"#ignore",
        "qualificationId":3,
        "qualNationalCode":"10218NAT",
        "qualTitle":"Aboriginal Language/s v2",
        "qualLevel":"Certificate I in",
        "isSufficientForQualification":true,
        "isVET":true,
        "fieldOfEducation":"0915",
        "reportableHours":20.0,
        "modules":[{"creditPoints":null,"expiryDays":null,"fieldOfEducation":null,"id":3,"isCustom":null,"type":"UNIT OF COMPETENCY","isOffered":true,"nationalCode":"AUM1001A","nominalHours":"#number","specialization":null,"title":"Manage personal career goals","createdOn":"#ignore","modifiedOn":"#ignore"}],
        "customFields":{},
        "rules":[],
        "isTraineeship":true,
        "currentlyOffered":false
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Course with only required fields

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post3",
        "fieldOfEducation":"123456",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course creating3",
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
        "isTraineeship":false,
        "feeHelpClass":false
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

        * def id = get[0] response.rows[?(@.values == ["post3"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":##number,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course creating3",
        "code":"post3",
        "tags":[],
        "enrolmentType":"Open enrolment",
        "allowWaitingLists":false,
        "dataCollectionRuleId":102,
        "dataCollectionRuleName":"Accredited course",
        "status":"Enabled",
        "brochureDescription":null,
        "currentClassesCount":0,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":0,
        "studentWaitingListCount":0,
        "hasEnrolments":false,
        "webDescription":null,
        "documents":[],
        "relatedSellables":[],
        "qualificationId":null,
        "qualNationalCode":null,
        "qualTitle":null,
        "qualLevel":null,
        "isSufficientForQualification":false,
        "isVET":true,
        "fieldOfEducation":"123456",
        "reportableHours":0.0,
        "modules":[],
        "customFields":{},
        "rules":[],
        "isTraineeship":false,
        "currentlyOffered":true
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create Course without Name

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post4",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Name is required."



    Scenario: (-) Create Course without Code

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating5",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Code is required."



    Scenario: (-) Create Course without dataCollectionRule

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post6",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating6",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":0,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":null,
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Data collection rule is required."



    Scenario: (-) Create Course with existing Code

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"course1",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating7",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Code must be unique."



    Scenario: (-) Create Course when Qualification is set and isVet is false

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post8",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating8",
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
        "qualificationId":3,
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Course must be VET if there is a qualification set."



    Scenario: (-) Create Course with "isSufficientForQualification":true if "qualificationId":null

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post9",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":false,
        "name":"Course creating9",
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
        "qualificationId":null,
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Course cannot be sufficient for a qualification if there is no qualification set."



    Scenario: (-) Create Course without isSufficientForQualification flag

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post10",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":null,
        "isVET":true,
        "name":"Course creating10",
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
        "qualificationId":3,
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Sufficient for qualification flag is required."



    Scenario: (-) Create Course Name of >200 symbols

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post11",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"A3A5A7A9A12A15A18A21A24A27A30A33A36A39A42A45A48A51A54A57A60A63A66A69A72A75A78A81A84A87A90A93A96A100A104A108A112A116A120A124A128A132A136A140A144A148A152A156A160A164A168A172A176A180A184A188A192A196A200A2",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Course name cannot be greater than 200 characters."



    Scenario: (-) Create Course Code of >32 symbols

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"A3A5A7A9A12A15A18A21A24A27A30A3AB",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating12",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 204



    Scenario: (-) Create Course with wrong Code

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"wrong%&*",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating13",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Course code must start and end with letters or numbers and can contain full stops."



    Scenario: (-) Create Course with wrong FieldOfEducation

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post14",
        "fieldOfEducation":"123",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating14",
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
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "The field of education must be empty or 6 characters long."



    Scenario: (-) Create Course when isTraineeship is null

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post15",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating15",
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
        "isTraineeship":null,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Course/Traineeship flag is required."



    Scenario: (-) Create Traineeship Course without Qualification

        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"post16",
        "fieldOfEducation":null,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course creating16",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":10,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "qualificationId":null,
        "isTraineeship":true,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Traineeship requires qualification to be set."



    Scenario: (-) Create Course when reportable Hours is null

        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"post14",
        "fieldOfEducation":"123456",
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course creating14",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":null,
        "webDescription":null,
        "customFields":{},
        "tags":[],
        "documents":[],
        "relatedSellables":[],
        "modules":[],
        "dataCollectionRuleId":"102",
        "isTraineeship":false,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Reportable hours is required."



    Scenario: (-) Create Traineeship Course with currentlyOffered:null

        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"post16",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course creating16",
        "enrolmentType":"Open enrolment",
        "status":"Enabled",
        "reportableHours":20,
        "webDescription":"some web description",
        "customFields":{},
        "tags":[{"id":227}],
        "documents":[{"id":200}],
        "relatedSellables":[{"id":1001,"name":"product1","code":"prd1","type":"Product","active":true}],
        "modules":[{"id":3}],
        "dataCollectionRuleId":"101",
        "brochureDescription":"some description",
        "qualificationId":3,
        "isTraineeship":true,
        "currentlyOffered":null,
        "feeHelpClass":false
        }
        """

        Given path ishPath
        And request newCourse
        When method POST
        Then status 400
        And match $.errorMessage == "Currently offered flag required for traineeship."



    Scenario: (+) Create Course by notadmin with access rights

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

        * def newCourse =
        """
        {
        "allowWaitingLists":true,
        "code":"post2",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":true,
        "isVET":true,
        "name":"Course creating2",
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
        "currentlyOffered":false,
        "feeHelpClass":false
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

        * def id = get[0] response.rows[?(@.values == ["post2"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course creating2",
        "code":"post2",
        "tags":[{"id":227,"name":"course1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "enrolmentType":"Open enrolment",
        "allowWaitingLists":true,
        "dataCollectionRuleId":101,
        "dataCollectionRuleName":"Non-accredited course",
        "status":"Course disabled",
        "brochureDescription":"some description",
        "currentClassesCount":0,
        "futureClasseCount":0,
        "selfPacedclassesCount":0,
        "unscheduledClasseCount":0,
        "passedClasseCount":0,
        "cancelledClassesCount":0,
        "studentWaitingListCount":0,
        "hasEnrolments":false,
        "webDescription":"some web description",
        "documents":[{"attachedRecordsCount":"#ignore", "id":200,"name":"defaultPrivateDocument","versionId":null,"added":"#ignore","tags":[],"thumbnail":null,"versions":[{"id":200,"added":"#ignore","createdBy":"onCourse Administrator","fileName":"defaultPrivateDocument.txt","mimeType":"text/plain","size":"22 b","url":null,"thumbnail":null}],"description":"Private description","access":"Private","shared":true,"createdOn":"#ignore","modifiedOn":"#ignore","removed":false,"attachmentRelations":"#ignore"}],
        "relatedSellables":"#ignore",
        "qualificationId":3,
        "qualNationalCode":"10218NAT",
        "qualTitle":"Aboriginal Language/s v2",
        "qualLevel":"Certificate I in",
        "isSufficientForQualification":true,
        "isVET":true,
        "fieldOfEducation":"0915",
        "reportableHours":20.0,
        "modules":[],
        "customFields":{},
        "rules":[],
        "isTraineeship":true,
        "currentlyOffered":false
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



    Scenario: (-) Create new Course by notadmin without access rights

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

        * def newCourse = {}

        Given path ishPath
        And request newCourse
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create course. Please contact your administrator"

