@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/outcome'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/outcome'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        


        
    Scenario: (+) Delete existing Outcome by admin

#       <----->  Add a new entity for deleting and get id:
        * def newOutcome =
        """
        {
        "id":null,
        "contactId":10,
        "enrolmentId":"107",
        "studentName":"stud4, stud4",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "startDate":"2020-02-01",
        "startDateOverridden":true,
        "endDate":"2020-02-29",
        "endDateOverridden":true,
        "reportableHours":22,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Not set",
        "hoursAttended":50,
        "vetPurchasingContractID":"123",
        "vetPurchasingContractScheduleID":"123",
        "vetFundingSourceStateID":"state",
        "specificProgramIdentifier":"del1",
        "isPriorLearning":false,
        "hasCertificate":null,
        "printed":false,
        "createdOn":null
        }
        """

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Outcome'
        And param columns = 'specificProgramIdentifier'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["del1"])].id
        * print "id = " + id
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (+) Delete existing Outcome by notadmin with rights

#       <----->  Add a new entity for deleting and get id:
        * def newOutcome =
        """
        {
        "id":null,
        "contactId":10,
        "enrolmentId":"107",
        "studentName":"stud4, stud4",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "startDate":"2020-02-01",
        "startDateOverridden":true,
        "endDate":"2020-02-29",
        "endDateOverridden":true,
        "reportableHours":22,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Not set",
        "hoursAttended":50,
        "vetPurchasingContractID":"123",
        "vetPurchasingContractScheduleID":"123",
        "vetFundingSourceStateID":"state",
        "specificProgramIdentifier":"del2",
        "isPriorLearning":false,
        "hasCertificate":null,
        "printed":false,
        "createdOn":null
        }
        """

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'Outcome'
        And param columns = 'specificProgramIdentifier'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["del2"])].id
        * print "id = " + id
#       <--->

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



    Scenario: (-) Delete existing Outcome by notadmin without rights

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

        Given path ishPath + '/108'
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete outcome. Please contact your administrator"



    Scenario: (-) Delete Outcome used in certificates

        Given path ishPath + '/106'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Cannot delete outcome used in certificate."



    Scenario: (-) Delete Outcome used in Avetmiss exports

        Given path ishPath + '/2'
        When method DELETE
        Then status 400
        And match $.errorMessage == "For audit reasons, you cannot delete outcomes linked to a successful AVETMISS export. Instead, mark the outcomes as 'do not report' or set an appropriate outcome status."



    Scenario: (-) Delete NOT existing Outcome

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (-) Delete Outcome with NULL as ID

        Given path ishPath + '/null'
        When method DELETE
        Then status 404

