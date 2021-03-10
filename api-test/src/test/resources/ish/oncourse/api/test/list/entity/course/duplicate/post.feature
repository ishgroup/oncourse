@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/course/duplicate'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/course/duplicate'
        * def ishPathCourse = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Duplicate Course by admin

        Given path ishPath
        And request [1,2]
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id1 = get[0] response.rows[?(@.values == ["course5"])].id
        * def id2 = get[0] response.rows[?(@.values == ["course6"])].id
        * print "ids = " + id1 + ', ' + id2

        Given path ishPathCourse + '/' + id1
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id1)",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "name":"Course1",
        "code":"course5",
        "tags":[],
        "enrolmentType":"Open enrolment",
        "allowWaitingLists":true,
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
        "qualificationId":1,
        "qualNationalCode":"UEE30807",
        "qualTitle":"Electrotechnology Electrician",
        "qualLevel":"Certificate III in",
        "isSufficientForQualification":false,
        "isVET":true,"fieldOfEducation":"0313",
        "reportableHours":0.0,
        "modules":[],
        "customFields":{},
        "rules":[],
        "isTraineeship":false,
        "currentlyOffered":true
        }
        """

        Given path ishPathCourse + '/' + id2
        When method GET
        Then status 200
        And match $ == {"id":"#(~~id2)","createdOn":"#ignore","modifiedOn":"#ignore","name":"Course2","code":"course6","tags":[],"enrolmentType":"Open enrolment","allowWaitingLists":true,"dataCollectionRuleId":102,"dataCollectionRuleName":"Accredited course","status":"Enabled","brochureDescription":null,"currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0,"studentWaitingListCount":0,"hasEnrolments":false,"webDescription":null,"documents":[],"relatedSellables":[],"qualificationId":null,"qualNationalCode":null,"qualTitle":null,"qualLevel":null,"isSufficientForQualification":false,"isVET":false,"fieldOfEducation":null,"reportableHours":0.0,"modules":[],"customFields":{},"rules":[],"isTraineeship":false,"currentlyOffered":true}

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathCourse + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathCourse + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (+) Duplicate Course by notadmin with access rights

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

        Given path ishPath
        And request [1,2]
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathList
        And param entity = 'Course'
        And param columns = 'code'
        When method GET
        Then status 200

        * def id1 = get[0] response.rows[?(@.values == ["course5"])].id
        * def id2 = get[0] response.rows[?(@.values == ["course6"])].id
        * print "ids = " + id1 + ', ' + id2

        Given path ishPathCourse + '/' + id1
        When method GET
        Then status 200
        And match $ == {"id":"#(~~id1)","createdOn":"#ignore","modifiedOn":"#ignore","name":"Course1","code":"course5","tags":[],"enrolmentType":"Open enrolment","allowWaitingLists":true,"dataCollectionRuleId":102,"dataCollectionRuleName":"Accredited course","status":"Enabled","brochureDescription":null,"currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0,"studentWaitingListCount":0,"hasEnrolments":false,"webDescription":null,"documents":[],"relatedSellables":[],"qualificationId":1,"qualNationalCode":"UEE30807","qualTitle":"Electrotechnology Electrician","qualLevel":"Certificate III in","isSufficientForQualification":false,"isVET":true,"fieldOfEducation":"0313","reportableHours":0.0,"modules":[],"customFields":{},"rules":[],"isTraineeship":false,"currentlyOffered":true}

        Given path ishPathCourse + '/' + id2
        When method GET
        Then status 200
        And match $ == {"id":"#(~~id2)","createdOn":"#ignore","modifiedOn":"#ignore","name":"Course2","code":"course6","tags":[],"enrolmentType":"Open enrolment","allowWaitingLists":true,"dataCollectionRuleId":102,"dataCollectionRuleName":"Accredited course","status":"Enabled","brochureDescription":null,"currentClassesCount":0,"futureClasseCount":0,"selfPacedclassesCount":0,"unscheduledClasseCount":0,"passedClasseCount":0,"cancelledClassesCount":0,"studentWaitingListCount":0,"hasEnrolments":false,"webDescription":null,"documents":[],"relatedSellables":[],"qualificationId":null,"qualNationalCode":null,"qualTitle":null,"qualLevel":null,"isSufficientForQualification":false,"isVET":false,"fieldOfEducation":null,"reportableHours":0.0,"modules":[],"customFields":{},"rules":[],"isTraineeship":false,"currentlyOffered":true}

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathCourse + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathCourse + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Duplicate Course by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        And request [1,2]
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to duplicate course. Please contact your administrator"



    Scenario: (-) Duplicate not existing Course

        Given path ishPath
        And request [99999]
        When method POST
        Then status 400
        And match $.errorMessage == "Course with id=99999 not found."