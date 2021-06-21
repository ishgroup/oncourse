@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/certificate/revoke'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate/revoke'
        * def ishPathCertificate = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        



    Scenario: (+) Revoke Certificate by admin

#       <----->  Add a new entity to revoke and get id:
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
        "privateNotes":"some private notes",
        "publicNotes":"some public notes 400",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPathCertificate
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'publicNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some public notes 400"])].id
        * print "id = " + id
#       <--->

        Given path ishPath
        And request {"ids":["#(~~id)"], "revokeReason":"some reason"}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathCertificate + '/' + id
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
        "privateNotes":"#ignore",
        "publicNotes":"some public notes 400",
        "awardedOn":"2019-06-18",
        "expiryDate":"2025-06-18",
        "issuedOn":"2029-06-18",
        "number":"#number",
        "printedOn":null,
        "revokedOn":"#ignore",
        "code":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """
        And match $.privateNotes contains "-- REVOKED ON"
        And match $.privateNotes contains "some reason"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathCertificate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Revoke Certificate by notadmin with access rights

#       <----->  Add a new entity to revoke and get id:
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
        "privateNotes":"some private notes",
        "publicNotes":"some public notes 401",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPathCertificate
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'publicNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some public notes 401"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        Given path ishPath
        And request {"ids":["#(~~id)"], "revokeReason":"some reason"}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathCertificate + '/' + id
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
        "privateNotes":"#ignore",
        "publicNotes":"some public notes 401",
        "awardedOn":"2019-06-18",
        "expiryDate":"2025-06-18",
        "issuedOn":"2029-06-18",
        "number":"#number",
        "printedOn":null,
        "revokedOn":"#ignore",
        "code":"#ignore",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """
        And match $.privateNotes contains "-- REVOKED ON"
        And match $.privateNotes contains "some reason"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathCertificate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Revoke two Certificates by admin

#       <----->  Add a new entities to revoke and get ids:
        * def newCertificate1 =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes",
        "publicNotes":"some public notes 402",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        * def newCertificate2 =
        """
        {
        "studentContactId":10,
        "studentName":"stud4",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2","level":"Certificate I in",
        "isQualification":false,
        "outcomes":[{"id":107}],
        "privateNotes":"some private notes",
        "publicNotes":"some public notes 403",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPathCertificate
        And request newCertificate1
        When method POST
        Then status 204

        Given path ishPathCertificate
        And request newCertificate2
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'publicNotes'
        When method GET
        Then status 200

        * def id1 = get[0] response.rows[?(@.values == ["some public notes 402"])].id
        * def id2 = get[0] response.rows[?(@.values == ["some public notes 403"])].id
        * print "id1 = " + id1
        * print "id2 = " + id2
#       <--->

        Given path ishPath
        And request {"ids":["#(~~id1)","#(~~id2)"], "revokeReason":"some reason for revoke"}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPathCertificate + '/' + id1
        When method GET
        Then status 200
        And match $.privateNotes contains "-- REVOKED ON"
        And match $.privateNotes contains "some reason for revoke"

        Given path ishPathCertificate + '/' + id2
        When method GET
        Then status 200
        And match $.privateNotes contains "-- REVOKED ON"
        And match $.privateNotes contains "some reason for revoke"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathCertificate + '/' + id1
        When method DELETE
        Then status 204

        Given path ishPathCertificate + '/' + id2
        When method DELETE
        Then status 204



    Scenario: (-) Revoke Certificate by notadmin without access rights

#       <----->  Add a new entity to revoke and get id:
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
        "privateNotes":"some private notes 405",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPathCertificate
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some private notes 405"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
#       <--->

        Given path ishPath
        And request {"ids":["#(~~id)"], "revokeReason":"some reason"}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to revoke certificate. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathCertificate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Revoke Certificate without reason

#       <----->  Add a new entity to revoke and get id:
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
        "privateNotes":"some private notes 424",
        "publicNotes":"some public notes",
        "awardedOn":"2019-06-18",
        "expiryDate":2025-06-18,
        "issuedOn":2029-06-18
        }
        """

        Given path ishPathCertificate
        And request newCertificate
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Certificate'
        And param columns = 'privateNotes'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["some private notes 424"])].id
        * print "id = " + id
#       <--->

        Given path ishPath
        And request {"ids":["#(~~id)"], "revokeReason":""}
        When method POST
        Then status 400
        And match $.errorMessage == "Reason for revoking is required."

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathCertificate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Revoke Certificate with status: revoked

        Given path ishPath
        And request {"ids":[1001], "revokeReason":"some reason"}
        When method POST
        Then status 400
        And match $.errorMessage == "Certificate with id = 1001 was revoked before."



    Scenario: (-) Revoke not existing Certificate

        Given path ishPath
        And request {"ids":[99999], "revokeReason":"some reason"}
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."