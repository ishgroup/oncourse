@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/certificate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Create Certificate by admin

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 100",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some private notes 100"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "studentContactId":10,
        "studentName":"stud4",
        "studentSuburb":null,
        "studentDateOfBirth":"1997-06-18",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2",
        "level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107,"issueDate":"2027-02-04","code":"AUM1001A","name":"Manage personal career goals","status":"Satisfactorily completed (81)"}],
        "privateNotes":"some private notes 100",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":"2025-06-18",
        "issuedOn":"2029-06-18",
        "number":"#number",
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Certificate ('Qualification or skill set' not set) without Qualification

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":null,
        "nationalCode":null,
        "title":null,
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 100a",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some private notes 100a"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "studentContactId":10,
        "studentName":"stud4",
        "studentSuburb":null,
        "studentDateOfBirth":"1997-06-18",
        "qualificationId":null,
        "nationalCode":null,
        "title":null,
        "level":null,
        "isQualification":false,
        "outcomes":[{"id":107,"issueDate":"2027-02-04","code":"AUM1001A","name":"Manage personal career goals","status":"Satisfactorily completed (81)"}],
        "privateNotes":"some private notes 100a",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":"2025-06-18",
        "issuedOn":"2029-06-18",
        "number":"#number",
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Certificate by notadmin with access rights

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

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 101",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some private notes 101"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#number",
        "studentContactId":10,
        "studentName":"stud4",
        "studentSuburb":null,
        "studentDateOfBirth":"1997-06-18",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2",
        "level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":107,"issueDate":"2027-02-04","code":"AUM1001A","name":"Manage personal career goals","status":"Satisfactorily completed (81)"}],
        "privateNotes":"some private notes 101",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":"2025-06-18",
        "issuedOn":"2029-06-18",
        "number":"#number",
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
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



    Scenario: (-) Create new Certificate by notadmin without access rights

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

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 102",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create certificate. Please contact your administrator"



    Scenario: (-) Create new Certificate with empty Contact

        * def newCertificate =
        """
        {
        "studentContactId":null,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 103",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Student is required."



    Scenario: (-) Create new Certificate without Outcomes

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[],
        "privateNotes":"some private notes 104",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Modules is required."



    Scenario: (-) Create new Certificate without awardedOn

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 105",
        "publicNotes":"some public notes",
        "awardedOn":null,
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Date of awarding is required."



    Scenario: (-) Create new Certificate with not existing Contact

        * def newCertificate =
        """
        {
        "studentContactId":99999,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 106",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Student  with contact id=99999 doesn't exist."



    Scenario: (-) Create new Certificate with not existing Qualification

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":99999,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 107",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Qualification  with id=99999 doesn't exist."



    Scenario: (-) Create new Certificate with not existing Outcome

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":99999}],
        "privateNotes":"some private notes 108",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Outcome with id=99999 doesn't exist."



    Scenario: (-) Create new Certificate for Contact without available VET outcomes

        * def newCertificate =
        """
        {
        "studentContactId":2,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 109",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Outcome with id=107 doesn't relate to this student."


    Scenario: (-) Create new Certificate for Outcome with NOT_SET outcome

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":119}],
        "privateNotes":"some private notes 109",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Outcome with code: 'AUM1503A' has NOT_SET status."


        Scenario: (-) Create Certificate (Qualification or skill set) for Contact without Qualification

        * def newCertificate =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":null,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 110",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 400
        And match $.errorMessage == "Qualification is required."