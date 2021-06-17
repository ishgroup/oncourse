@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/certificate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Update Certificate by admin

#       <----->  Add a new entity to update and define its id:
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
        "privateNotes":"some private notes 200",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 200"])].id
        * print "id = " + id
#       <--->

        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":4,
        "isQualification":true,
        "outcomes":[{"id":108}],
        "privateNotes":"some private notes 200 upd",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":2023-07-19,
        "issuedOn":2027-05-19
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "studentContactId":10,
        "studentName":"stud4",
        "studentSuburb":null,
        "studentDateOfBirth":"1997-06-18",
        "qualificationId":4,
        "nationalCode":"90946NSW",
        "title":"Building Studies, Technology",
        "level":"Advanced Diploma of",
        "isQualification":true,
        "outcomes":[{"id":108,"issueDate":"2025-10-05","code":"AUM1503A","name":"Create new product designs","status":"Competency achieved/pass (20)"}],
        "privateNotes":"some private notes 200 upd",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":"2023-07-19",
        "issuedOn":"2027-05-19",
        "number":"#ignore",
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



    Scenario: (+) Update Certificate by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
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
        "privateNotes":"some private notes 201",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 201"])].id
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

        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":4,
        "isQualification":true,
        "outcomes":[{"id":108}],
        "privateNotes":"some private notes 201 upd",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":2023-07-19,
        "issuedOn":2027-05-19
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#number",
        "studentContactId":10,
        "studentName":"stud4",
        "studentSuburb":null,
        "studentDateOfBirth":"1997-06-18",
        "qualificationId":4,
        "nationalCode":"90946NSW",
        "title":"Building Studies, Technology",
        "level":"Advanced Diploma of",
        "isQualification":true,
        "outcomes":[{"id":108,"issueDate":"2025-10-05","code":"AUM1503A","name":"Create new product designs","status":"Competency achieved/pass (20)"}],
        "privateNotes":"some private notes 201 upd",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":"2023-07-19",
        "issuedOn":"2027-05-19",
        "number":"#ignore",
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



    Scenario: (-) Update Certificate by notadmin without access rights

#       <----->  Add a new entity to update and define its id:
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
        "privateNotes":"some private notes 202",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 202"])].id
        * print "id = " + id

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

        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":4,
        "isQualification":true,
        "outcomes":[{"id":108}],
        "privateNotes":"some private notes 202 upd",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":2023-07-19,
        "issuedOn":2027-05-19
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit certificate. Please contact your administrator"

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



    Scenario: (-) Update Certificate required fields to empty

#       <----->  Add a new entity to update and define its id:
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
        "privateNotes":"some private notes 203",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 203"])].id
        * print "id = " + id

#       <--->  Update Certificate to empty Contact:
        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":null,
        "studentName":"stud4",
        "qualificationId":3,
        "isQualification":true,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 203",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Student is required."

#       <--->  Update Certificate to empty awardedOn:
        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "isQualification":true,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 203",
        "publicNotes":"some public notes",
        "awardedOn":null,
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Date of awarding is required."

#       <--->  Update Certificate to empty Outcomes:
        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "isQualification":true,
        "outcomes":[],
        "privateNotes":"some private notes 203",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Modules is required."

#       <--->  Update Certificate to empty Qualification when "isQualification":true:
        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":null,
        "isQualification":true,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 203",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Qualification is required."

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




    Scenario: (-) Update Certificate Code to not existing Qualification

#       <----->  Add a new entity to update and define its id:
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
        "privateNotes":"some private notes 204",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 204"])].id
        * print "id = " + id
#       <--->

        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":99999,
        "isQualification":true,
        "outcomes":[{"id":108}],
        "privateNotes":"some private notes 204",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":2023-07-19,
        "issuedOn":2027-05-19
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Qualification  with id=99999 doesn't exist."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update Certificate Code to not existing Outcome

#       <----->  Add a new entity to update and define its id:
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
        "privateNotes":"some private notes 205",
        "publicNotes":"some public notes",
        "awardedOn":"2022-07-07",
        "expiryDate":2023-07-18,
        "issuedOn":2027-05-18
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 205"])].id
        * print "id = " + id
#       <--->

        * def certificateToUpdate =
        """
        {
        "id":"#(id)",
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "isQualification":true,
        "outcomes":[{"id":99999}],
        "privateNotes":"some private notes 205",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":2023-07-19,
        "issuedOn":2027-05-19
        }
        """

        Given path ishPath + '/' + id
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Outcome with id=99999 doesn't exist."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update disabled fields in revoked Certificate

#       <---> Change studentContactId,studentName,qualificationId,nationalCode,title,level,isQualification,outcomes,number,printedOn,revokedOn:
        * def certificateToUpdate =
        """
        {
        "id":1001,
        "studentContactId":10,
        "studentName":"stud4",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"2002-04-30",
        "qualificationId":3,
        "nationalCode":"UEE30807",
        "title":"Electrotechnology Electrician",
        "level":"Certificate III in",
        "isQualification":true,
        "outcomes":[{"id":108}],
        "privateNotes":"private notes 2",
        "publicNotes":"public notes 2",
        "awardedOn":"2018-05-18",
        "expiryDate":"2019-07-17",
        "issuedOn":"2018-05-18",
        "number":20,
        "printedOn":"2019-01-19",
        "revokedOn":"2019-01-20",
        "code":null
        }
        """

        Given path ishPath + '/1001'
        And request certificateToUpdate
        When method PUT
        Then status 204

#       <---> Assertion (Disabled fields should not be changed):
        Given path ishPath + '/1001'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1001,
        "studentContactId":4,
        "studentName":"stud3",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"2002-05-01",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2",
        "level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":106,"issueDate":"2025-10-05","code":"AUM1001A","name":"Manage personal career goals","status":"Not set"}],
        "privateNotes":"private notes 2",
        "publicNotes":"public notes 2",
        "awardedOn":"2018-05-18",
        "expiryDate":"2019-07-17",
        "issuedOn":"2018-05-18",
        "number":2,
        "printedOn":"2018-05-19",
        "revokedOn":"2018-05-20",
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """



    Scenario: (-) Update not existing Certificate

        * def certificateToUpdate =
        """
        {
        "id":99999,
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "isQualification":true,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes 206",
        "publicNotes":"some public notes upd",
        "awardedOn":"2022-07-08",
        "expiryDate":2023-07-19,
        "issuedOn":2027-05-19
        }
        """

        Given path ishPath + '/99999'
        And request certificateToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."




