@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/certificate'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Certificates by admin

        Given path ishPathList
        And param entity = 'Certificate'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003","1004"]



    Scenario: (+) Get list of all Certificates by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Certificate'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1000","1001","1002","1003","1004"]



    Scenario: (+) Get Certificate by admin

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "studentContactId":4,
        "studentName":"stud3",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"2002-05-01",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":106,"issueDate":"2025-10-05","code":"AUM1001A","name":"Manage personal career goals","status":"Not set"}],
        "privateNotes":"private notes",
        "publicNotes":"public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":null,
        "issuedOn":null,
        "number":1,
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Get Certificate ('Qualification or skill set' not set) without Qualification

        Given path ishPath + '/1002'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1002,
        "studentContactId":4,
        "studentName":"stud3",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"2002-05-01",
        "qualificationId":null,
        "nationalCode":null,
        "title":null,
        "level":null,
        "isQualification":false,
        "outcomes":[{"id":106,"issueDate":"2025-10-05","code":"AUM1001A","name":"Manage personal career goals","status":"Not set"}],
        "privateNotes":"private notes",
        "publicNotes":"public notes",
        "awardedOn":"2017-06-17",
        "expiryDate":null,
        "issuedOn":null,
        "number":3,
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Get printed Certificate

        Given path ishPath + '/1004'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1004,
        "studentContactId":4,
        "studentName":"stud3",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"2002-05-01",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107,"issueDate":"2027-02-04","code":"AUM1001A","name":"Manage personal career goals","status":"Satisfactorily completed (81)"}],
        "privateNotes":"private notes 2",
        "publicNotes":"public notes 2",
        "awardedOn":"2018-05-18",
        "expiryDate":null,
        "issuedOn":"2018-05-19",
        "number":5,
        "printedOn":"2018-05-19",
        "revokedOn":null,
        "code":"cBxWbE3dFwT4",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (+) Get Certificate by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/1000'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1000,
        "studentContactId":4,
        "studentName":"stud3",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"2002-05-01",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":106,"issueDate":"2025-10-05","code":"AUM1001A","name":"Manage personal career goals","status":"Not set"}],
        "privateNotes":"private notes",
        "publicNotes":"public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":null,
        "issuedOn":null,
        "number":1,
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (-) Get Certificate by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/1000'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get certificate. Please contact your administrator"



    Scenario: (-) Get not existing Certificate

        Given path ishPath + "/9999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '9999' doesn't exist."

