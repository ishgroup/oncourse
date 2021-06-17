@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/certificate/enrolment'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate/enrolment'
        * def ishPathCertificate = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        



    Scenario: (+) Add Certificate by admin

        * def newCertificate = {"enrolmentIds":["114"],"createStatementOfAtteiment":true}

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 200

        * def id = get[0] response
        * print "id = " + id

        Given path ishPathCertificate + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "studentContactId":29,
        "studentName":"student2 PaymentOut",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"1995-06-06",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2",
        "level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":117,"issueDate":"2025-10-05","code":"AUM1001A","name":"Manage personal career goals","status":"Competency achieved/pass (20)"}],
        "privateNotes":null,
        "publicNotes":null,
        "awardedOn":"#ignore",
        "expiryDate":null,
        "issuedOn":null,
        "number":"#number",
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathCertificate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add Certificate by notadmin with access rights

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

        * def newCertificate = {"enrolmentIds":["114"],"createStatementOfAtteiment":true}

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 200

        * def id = get[0] response
        * print "id = " + id

        Given path ishPathCertificate + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "studentContactId":29,
        "studentName":"student2 PaymentOut",
        "studentSuburb":"Adelaide",
        "studentDateOfBirth":"1995-06-06",
        "qualificationId":3,
        "nationalCode":"10218NAT",
        "title":"Aboriginal Language/s v2",
        "level":"Certificate I in",
        "isQualification":true,
        "outcomes":[{"id":117,"issueDate":"2025-10-05","code":"AUM1001A","name":"Manage personal career goals","status":"Competency achieved/pass (20)"}],
        "privateNotes":null,
        "publicNotes":null,
        "awardedOn":"#ignore",
        "expiryDate":null,
        "issuedOn":null,
        "number":"#number",
        "printedOn":null,
        "revokedOn":null,
        "code":null,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPathCertificate + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Add Certificate by notadmin without access rights

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

        * def newCertificate = {"enrolmentIds":["114"],"createStatementOfAtteiment":true}

        Given path ishPath
        And request newCertificate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create certificates. Please contact your administrator"



#    Scenario: (+) Add Certificate for not existing Enrolment
#
#        * def newCertificate = {"enrolmentIds":["99999"],"createStatementOfAtteiment":true}
#
#        Given path ishPath
#        And request newCertificate
#        When method POST
#        Then status 400
#        And match $.errorMessage == ""

