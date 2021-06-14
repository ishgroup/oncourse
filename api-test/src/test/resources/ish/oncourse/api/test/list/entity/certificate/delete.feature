@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/certificate'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/certificate'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing Certificate by admin

#       <----->  Add a new entity for deleting and get id:
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 200"])].id
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



    Scenario: (+) Delete existing Certificate by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 201"])].id
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



    Scenario: (-) Delete existing Certificate by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
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

        * def id = get[0] response.rows[?(@.values == ["some private notes 202"])].id
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
        And match $.errorMessage == "Sorry, you have no permissions to delete certificate. Please contact your administrator"

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



    Scenario: (-) Delete printed Certificate

        Given path ishPath + '/1001'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Printed certificates cannot be deleted."



    Scenario: (-) Delete NOT existing Certificate

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."



