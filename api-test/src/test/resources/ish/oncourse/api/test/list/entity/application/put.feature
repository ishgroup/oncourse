@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/application'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/application'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update Application by admin

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"Offered",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 300",
        "documents":[{"id":201}],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 300"])].id
        * print "id = " + id
#       <--->

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":80.00,
        "enrolBy":"2031-09-10",
        "reason":"Some reason 300 UPD",
        "documents":[],
        "tags":[{"id":225}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
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
        "feeOverride":80.00,
        "enrolBy":"2031-09-10",
        "createdBy":"onCourse Administrator",
        "reason":"Some reason 300 UPD",
        "documents":[],
        "tags":[{"id":225,"name":"app1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+-) Update Application status

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 3018"])].id
        * print "id = " + id

#       <---> Change status to Offered:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"Offered",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.status == "Offered"

#       <---> Change status to Rejected:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"Rejected",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.status == "Rejected"

#       <---> Change status to Withdrawn:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"Withdrawn",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.status == "Withdrawn"

#       <---> Change status to In progress:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"In progress",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.status == "In progress"

#       <---> Change status to Accepted:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"Accepted",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $.status == "Accepted"

#       <---> Change status from Accepted to another (forbidden):
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 3018",
        "documents":[{"id":201}],
        "tags":[{"id":224}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Cannot change status for accepted applications."



    Scenario: (-) Update Application required fields to empty

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 303",
        "documents":[],
        "tags":[],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 303"])].id
        * print "id = " + id

#       <--->  Update Application to empty Contact:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":null,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 303",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Student is required."

#       <--->  Update Application to empty Course:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":null,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 303",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Course is required."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Application to other Contact

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 304",
        "documents":[],
        "tags":[],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 304"])].id
        * print "id = " + id

#       <--->  Update Application to other Contact:
        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":3,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 304",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Cannot change student for application."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



        Scenario: (-) Update Application course, createdBy, source

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 444",
        "documents":[],
        "tags":[],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 444"])].id
        * print "id = " + id
#       <--->

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":3,
        "applicationDate":"2015-01-01",
        "status":"New",
        "source":"another office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "createdBy":"another user",
        "reason":"Some reason 444",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

#       <---> Verification: 'course', 'applicationDate', 'createdBy', 'source' should not be changed
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
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
        "enrolBy":"2031-09-09",
        "createdBy":"onCourse Administrator",
        "reason":"Some reason 444",
        "documents":[],
        "tags":[],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Application

#       <----->  Add a new entity to update and define its id:
        * def applicationToUpdate =
        """
        {
        "id":99999,
        "contactId":4,
        "courseId":3,
        "applicationDate":"2015-01-01",
        "status":"New",
        "source":"another office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "createdBy":"another user",
        "reason":"Some reason 444",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath + '/99999'
        And request applicationToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Update Application by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"Offered",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 301",
        "documents":[{"id":201}],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 301"])].id
        * print "id = " + id

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":80.00,
        "enrolBy":"2031-09-10",
        "reason":"Some reason 301 UPD",
        "documents":[],
        "tags":[{"id":225}],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
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
        "feeOverride":80.00,
        "enrolBy":"2031-09-10",
        "createdBy":"onCourse Administrator",
        "reason":"Some reason 301 UPD",
        "documents":[],
        "tags":[{"id":225,"name":"app1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

        Given path '/logout'
        And request {}
        When method PUT
        
#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Application by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
        * def newApplication =
        """
        {
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":100.00,
        "enrolBy":"2031-09-09",
        "reason":"Some reason 302",
        "documents":[],
        "tags":[],
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

        * def id = get[0] response.rows[?(@.values == ["Some reason 302"])].id
        * print "id = " + id

        Given path '/logout'
        And request {}
        When method PUT
        
#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        * def applicationToUpdate =
        """
        {
        "id":"#(id)",
        "contactId":4,
        "courseId":4,
        "status":"New",
        "source":"office",
        "feeOverride":80.00,
        "enrolBy":"2031-09-10",
        "reason":"Some reason 301 UPD",
        "documents":[],
        "tags":[],
        "customFields":{}
        }
        """

        Given path ishPath + '/' + id
        And request applicationToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit application. Please contact your administrator"

        Given path '/logout'
        And request {}
        When method PUT
        
#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

