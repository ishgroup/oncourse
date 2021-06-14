@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/application'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/application'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create Application by admin

        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-01-01",
        "reason":"Some reason 100",
        "documents":[{"id":200}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 100"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "contactId":4,
        "studentName":"stud3",
        "courseId":4,
        "courseName":"Course4 course4",
        "applicationDate":"#ignore",
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-01-01",
        "createdBy":"onCourse Administrator",
        "reason":"Some reason 100",
        "documents":[{"attachedRecordsCount":"#ignore", "id":200,"name":"defaultPrivateDocument","versionId":null,"added":"#ignore","tags":[],"thumbnail":null,"versions":[{"id":200,"added":"#ignore","createdBy":"onCourse Administrator","fileName":"defaultPrivateDocument.txt","mimeType":"text/plain","size":"22 b","url":null,"thumbnail":null}],"description":"Private description","access":"Private","shared":true,"createdOn":"#ignore","modifiedOn":"#ignore","removed":false,"attachmentRelations":"#ignore"}],
        "tags":[{"id":224,"name":"app2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <---->  Scenario have been finished. Now remove attached document and then delete created entity:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-01-01",
        "reason":"Some reason 100",
        "documents":[],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204


    Scenario: (-) Create new Application with empty Contact

        * def newApplication =
        """
        {
        "contactId":null,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-03-03",
        "reason":"Some reason 103",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 400
        And match $.errorMessage == "Student is required."



    Scenario: (-) Create new Application with empty Course

        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":null,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-03-03",
        "reason":"Some reason 104",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 400
        And match $.errorMessage == "Course is required."



    Scenario: (-) Create new Application for not student

        * def newApplication =
        """
        {
        "contactId":1,
        "courseId":4,
        "status":"Offered",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-03-03",
        "reason":"Some reason 105",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 400
        And match $.errorMessage == "Contact is not a student."



    Scenario: (-) Create new Application for not existing contact

        * def newApplication =
        """
        {
        "contactId":99999,
        "courseId":4,
        "status":"Offered",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-03-03",
        "reason":"Some reason 106",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 400
        And match $.errorMessage == "Contact is not a student."



    Scenario: (-) Create new Application for not existing course

        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":99999,
        "status":"Offered",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-03-03",
        "reason":"Some reason 107",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 400
        And match $.errorMessage == "Course with id=99999 doesn't exist."


    Scenario: (+) Create Application by notadmin with access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-02-02",
        "reason":"Some reason 101",
        "documents":[],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Application'
        And param columns = 'reason'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["Some reason 101"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "contactId":3,
        "studentName":"stud2",
        "courseId":4,
        "courseName":"Course4 course4",
        "applicationDate":"#ignore",
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-02-02",
        "createdBy":"UserWithRightsCreate UserWithRightsCreate",
        "reason":"Some reason 101",
        "documents":[],
        "tags":[{"id":224,"name":"app2","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

        Given path '/logout'
        And request {}
        When method PUT
        
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



    Scenario: (-) Create new Application by notadmin without access rights

        Given path '/logout'
        And request {}
        When method PUT
        
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

        * def newApplication =
        """
        {
        "contactId":3,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2033-03-03",
        "reason":"Some reason 102",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath
        And request newApplication
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create application. Please contact your administrator"


