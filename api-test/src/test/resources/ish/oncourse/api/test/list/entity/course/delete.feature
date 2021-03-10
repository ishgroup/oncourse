@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/course'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/course'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'


        
    Scenario: (+) Delete existing Course by admin

#       <----->  Add a new entity for deleting and get id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"delete1",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course delete1",
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

        * def id = get[0] response.rows[?(@.values == ["delete1"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete Traineeship Course by admin

#       <----->  Add a new entity for deleting and get id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"delete1t",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":true,
        "name":"Course delete1t",
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

        * def id = get[0] response.rows[?(@.values == ["delete1t"])].id
        * print "id = " + id
#       <----->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing Course by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"delete2",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course delete2",
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

        * def id = get[0] response.rows[?(@.values == ["delete2"])].id
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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete existing Course by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newCourse =
        """
        {
        "allowWaitingLists":false,
        "code":"delete3",
        "fieldOfEducation":null,
        "id":0,
        "isSufficientForQualification":false,
        "isVET":false,
        "name":"Course delete3",
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

        * def id = get[0] response.rows[?(@.values == ["delete3"])].id
        * print "id = " + id

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

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete course. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
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



    Scenario: (-) Delete existing Course with relation

        Given path ishPath + '/1'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Course cannot be deleted because it has classes."



    Scenario: (-) Delete NOT existing Course

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

