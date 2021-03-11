@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/outcome'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/outcome'
        * def ishPathLogin = 'login'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Create Outcome by admin

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
        "specificProgramIdentifier":"pi1",
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

        * def id = get[0] response.rows[?(@.values == ["pi1"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + "/" + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":10,
        "enrolmentId":107,
        "studentName":"stud4",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "trainingPlanStartDate":"2027-02-01",
        "startDate":"2020-02-01",
        "startDateOverridden":true,
        "trainingPlanEndDate":"2027-02-05",
        "endDate":"2020-02-29",
        "endDateOverridden":true,
        "reportableHours":22.0,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Not set",
        "hoursAttended":50,
        "vetPurchasingContractID":"123",
        "vetPurchasingContractScheduleID":"123",
        "vetFundingSourceStateID":"state",
        "specificProgramIdentifier":"pi1",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "actualStartDate":"2027-02-01",
        "actualEndDate":"2027-02-05"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Outcome by notadmin with access rights

#       <--->  Login as notadmin:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

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
        "startDate":"2020-02-05",
        "startDateOverridden":true,
        "endDate":"2020-02-25",
        "endDateOverridden":true,
        "reportableHours":22,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Not set",
        "hoursAttended":50,
        "vetPurchasingContractID":"123",
        "vetPurchasingContractScheduleID":"123",
        "vetFundingSourceStateID":"state",
        "specificProgramIdentifier":"pi2",
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

        * def id = get[0] response.rows[?(@.values == ["pi2"])].id
        * print "id = " + id

#       <---> Verification:
        Given path ishPath + "/" + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "contactId":10,
        "enrolmentId":107,
        "studentName":"stud4",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "trainingPlanStartDate":"2027-02-01",
        "startDate":"2020-02-05",
        "startDateOverridden":true,
        "trainingPlanEndDate":"2027-02-05",
        "endDate":"2020-02-25",
        "endDateOverridden":true,
        "reportableHours":22.0,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Not set",
        "hoursAttended":50,
        "vetPurchasingContractID":"123",
        "vetPurchasingContractScheduleID":"123",
        "vetFundingSourceStateID":"state",
        "specificProgramIdentifier":"pi2",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "actualStartDate":"2027-02-01",
        "actualEndDate":"2027-02-05"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create Outcome by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def newOutcome = {"id":null,"contactId":10,"enrolmentId":"107","studentName":"stud4, stud4","moduleId":1,"moduleCode":"AUM1602A","moduleName":"Install plant, equipment or systems - Advanced","startDate":"2020-02-01","endDate":"2020-02-29","reportableHours":22,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":50,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"state","specificProgramIdentifier":"pi3","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create outcome. Please contact your administrator"



    Scenario: (-) Create Outcome if module is already part of different outcome

        * def newOutcome = {"id":null,"contactId":10,"enrolmentId":"107","studentName":"stud4, stud4","moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-29","reportableHours":100,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":50,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"state","specificProgramIdentifier":"pi4","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 400
        And match $.errorMessage == "This module is already part of different outcome."



    Scenario: (-) Create Outcome for not existing enrolment

        * def newOutcome = {"id":null,"contactId":10,"enrolmentId":"99999","studentName":"stud4, stud4","moduleId":1,"moduleCode":"AUM1602A","moduleName":"Install plant, equipment or systems - Advanced","startDate":"2020-02-01","endDate":"2020-02-29","reportableHours":22,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":50,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"state","specificProgramIdentifier":"pi5","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 400
        And match $.errorMessage == "Enrolment with id=99999 doesn't exist."



    Scenario: (-) Create Outcome with mixing VET and non-VET outcomes

        * def newOutcome = {"id":null,"contactId":29,"enrolmentId":"114","studentName":"PaymentOut, student2","moduleId":null,"moduleCode":null,"moduleName":null,"startDate":null,"endDate":null,"reportableHours":12,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":0,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"vetFundingSourceStateID":null,"specificProgramIdentifier":null,"isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 400
        And match $.errorMessage == "VET and non-VET outcomes cannot be mixed."



    Scenario: (-) Create Outcome when endDate < startDate

        * def newOutcome = {"id":null,"contactId":10,"enrolmentId":"107","studentName":"stud4, stud4","moduleId":1,"moduleCode":"AUM1602A","moduleName":"Install plant, equipment or systems - Advanced","startDate":"2020-02-29","startDateOverridden":true,"endDate":"2020-02-28","endDateOverridden":true,"reportableHours":22,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":50,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"state","specificProgramIdentifier":"pi6","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}

        Given path ishPath
        And request newOutcome
        When method POST
        Then status 400
        And match $.errorMessage == "End date can not be before the start date."



#    Scenario: (-) Create Outcome with empty startDate/endDate
#
##       <---> Empty statrtDate:
#        * def newOutcome = {"id":null,"contactId":10,"enrolmentId":"107","studentName":"stud4, stud4","moduleId":1,"moduleCode":"AUM1602A","moduleName":"Install plant, equipment or systems - Advanced","startDate":null,"startDateOverridden":true,"endDate":"2020-02-28","endDateOverridden":true,"reportableHours":22,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":50,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"state","specificProgramIdentifier":"pi7","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}
#
#        Given path ishPath
#        And request newOutcome
#        When method POST
#        Then status 400
#        And match $.errorMessage == ""
#
##       <---> Empty endDate:
#        * def newOutcome = {"id":null,"contactId":10,"enrolmentId":"107","studentName":"stud4, stud4","moduleId":1,"moduleCode":"AUM1602A","moduleName":"Install plant, equipment or systems - Advanced","startDate":"2020-02-29","endDate":null,"reportableHours":22,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":50,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"state","specificProgramIdentifier":"pi8","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}
#
#        Given path ishPath
#        And request newOutcome
#        When method POST
#        Then status 400
#        And match $.errorMessage == ""