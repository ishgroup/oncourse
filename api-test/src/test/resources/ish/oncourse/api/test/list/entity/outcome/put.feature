@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/outcome'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPathLogin = 'login'
        * def ishPath = 'list/entity/outcome'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



        * def outcomeToDefault = {"id":2,"contactId":2,"enrolmentId":3,"studentName":"stud1","moduleId":null,"moduleCode":null,"moduleName":null,"startDate":"2018-12-01","endDate":"2027-12-01","reportableHours":10,"deliveryMode":"Classroom","fundingSource":"Domestic full fee paying student","status":"Not set","hoursAttended":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"vetFundingSourceStateID":null,"specificProgramIdentifier":null,"isPriorLearning":false,"hasCertificate":false,"printed":false}


    Scenario: (+) Update Outcome by admin

        * def outcomeToUpdate =
        """
        {
        "id":2,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud1",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "startDate":"2018-12-02",
        "startDateOverridden":true,
        "endDate":"2027-12-02",
        "endDateOverridden":true,
        "reportableHours":"33",
        "deliveryMode":"Online",
        "fundingSource":"State - specific","status":"Competency achieved/pass (20)",
        "hoursAttended":"44",
        "specificProgramIdentifier":"123Abc",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false
        }
        """

        Given path ishPath + '/2'
        And request outcomeToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud1",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "trainingPlanStartDate":"2018-12-01",
        "startDate":"2018-12-02",
        "startDateOverridden":true,
        "trainingPlanEndDate":"2027-12-01",
        "endDate":"2027-12-02",
        "endDateOverridden":true,
        "reportableHours":33.0,
        "deliveryMode":"Online",
        "fundingSource":"State - specific",
        "status":"Competency achieved/pass (20)",
        "hoursAttended":44,
        "vetPurchasingContractID":null,
        "vetPurchasingContractScheduleID":null,
        "vetFundingSourceStateID":null,
        "specificProgramIdentifier":"123Abc",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "actualStartDate":"2018-12-01",
        "actualEndDate":"2027-12-01"
        }
        """

#       <--->  Scenario have been finished. Now change entity back:
        Given path ishPath + '/2'
        And request outcomeToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Outcome by notadmin with access rights

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

        * def outcomeToUpdate =
        """
        {
        "id":2,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud1",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "startDate":"2018-12-02",
        "startDateOverridden":true,
        "endDate":"2027-12-02",
        "endDateOverridden":true,
        "reportableHours":"33",
        "deliveryMode":"Online",
        "fundingSource":"State - specific","status":"Competency achieved/pass (20)",
        "hoursAttended":"44",
        "specificProgramIdentifier":"123Abc",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false
        }
        """

        Given path ishPath + '/2'
        And request outcomeToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":2,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud1",
        "moduleId":1,
        "moduleCode":"AUM1602A",
        "moduleName":"Install plant, equipment or systems - Advanced",
        "trainingPlanStartDate":"2018-12-01",
        "startDate":"2018-12-02",
        "startDateOverridden":true,
        "trainingPlanEndDate":"2027-12-01",
        "endDate":"2027-12-02",
        "endDateOverridden":true,
        "reportableHours":33.0,
        "deliveryMode":"Online",
        "fundingSource":"State - specific",
        "status":"Competency achieved/pass (20)",
        "hoursAttended":44,
        "vetPurchasingContractID":null,
        "vetPurchasingContractScheduleID":null,
        "vetFundingSourceStateID":null,
        "specificProgramIdentifier":"123Abc",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "actualStartDate":"2018-12-01",
        "actualEndDate":"2027-12-01"
        }
        """

#       <--->  Scenario have been finished. Now change entity back:
        Given path ishPath + '/2'
        And request outcomeToDefault
        When method PUT
        Then status 204



    Scenario: (+) Update Outcome without related certificate to empty module

        * def outcomeToUpdate =
        """
        {
        "id":2,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud1",
        "moduleId":null,
        "startDate":"2018-12-02",
        "startDateOverridden":true,
        "endDate":"2027-12-02",
        "endDateOverridden":true,
        "reportableHours":"33",
        "deliveryMode":"Online",
        "fundingSource":"State - specific","status":"Competency achieved/pass (20)",
        "hoursAttended":"44",
        "specificProgramIdentifier":"123Abc",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false
        }
        """

        Given path ishPath + '/2'
        And request outcomeToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":2,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud1",
        "moduleId":null,
        "moduleCode":null,
        "moduleName":null,
        "trainingPlanStartDate":"2018-12-01",
        "startDate":"2018-12-02",
        "startDateOverridden":true,
        "trainingPlanEndDate":"2027-12-01",
        "endDate":"2027-12-02",
        "endDateOverridden":true,
        "reportableHours":33.0,
        "deliveryMode":"Online",
        "fundingSource":"State - specific",
        "status":"Competency achieved/pass (20)",
        "hoursAttended":44,
        "specificProgramIdentifier":"123Abc",
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore"
        }
        """

#       <--->  Scenario have been finished. Now change entity back:
        Given path ishPath + '/2'
        And request outcomeToDefault
        When method PUT
        Then status 204



    Scenario: (-) Update Outcome by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def outcomeToUpdate = {}

        Given path ishPath + '/106'
        And request outcomeToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit outcome. Please contact your administrator"



    Scenario: (-) Update Outcome when startDate > endDate

        * def outcomeToUpdate =
        """
        {
        "id":106,
        "enrolmentId":106,
        "moduleId":3,
        "startDate":"2017-05-25",
        "startDateOverridden":true,
        "endDate":"2017-05-15",
        "endDateOverridden":true,
        "reportableHours":12.0,
        "deliveryMode":null,
        "fundingSource":null,
        "status":"Not set",
        "hoursAttended":null,
        "specificProgramIdentifier":null
        }
        """

        Given path ishPath + '/106'
        And request outcomeToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "End date can not be before the start date."



    Scenario: (-) Update module in Outcome with printed certificate

        * def outcomeToUpdate =
        """
        {
        "id":107,
        "enrolmentId":107,
        "studentName":"stud4",
        "moduleId":4,
        "startDate":null,
        "endDate":null,
        "reportableHours":null,
        "deliveryMode":null,
        "fundingSource":null,
        "status":"Satisfactorily completed (81)",
        "hoursAttended":null,
        "specificProgramIdentifier":null,
        "isPriorLearning":false
        }
        """

        Given path ishPath + '/107'
        And request outcomeToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Module cannot be changed for outcome used in certificate"



    Scenario: (-) Update module in Outcome with related (not printed) certificate

        * def outcomeToUpdate =
        """
        {
        "id":114,
        "enrolmentId":112,
        "studentName":"mergeB, studentB B",
        "moduleId":4,
        "startDate":null,
        "endDate":null,
        "reportableHours":null,
        "deliveryMode":null,
        "fundingSource":null,
        "status":"Not set",
        "hoursAttended":null,
        "specificProgramIdentifier":null,
        "isPriorLearning":false
        }
        """

        Given path ishPath + '/107'
        And request outcomeToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Module cannot be changed for outcome used in certificate"



    Scenario: (-) Update Status in Outcome with printed certificate

        * def outcomeToUpdate =
        """
        {
        "id":107,
        "enrolmentId":107,
        "studentName":"stud4",
        "moduleId":3,
        "startDate":null,
        "endDate":null,
        "reportableHours":null,
        "deliveryMode":null,
        "fundingSource":null,
        "status":"Withdrawn (40)",
        "hoursAttended":null,
        "specificProgramIdentifier":null,
        "isPriorLearning":false
        }
        """

        Given path ishPath + '/107'
        And request outcomeToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Can not change status for Outcome Id:107. Status cannot be changed for outcome used in printed certificate"



    Scenario: (-) Update Outcome to not existing module

        * def outcomeToUpdate =
        """
        {
        "id":106,
        "enrolmentId":106,
        "moduleId":99999,
        "startDate":"2017-05-15",
        "endDate":"2017-05-25",
        "reportableHours":12.0,
        "deliveryMode":null,
        "fundingSource":null,
        "status":"Not set",
        "hoursAttended":null,
        "specificProgramIdentifier":null
        }
        """

        Given path ishPath + '/106'
        And request outcomeToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Module with id=99999 doesn't exist."



    Scenario: (-) Update not existing Outcome

        * def outcomeToUpdate =
        """
        {
        "id":99999,
        "enrolmentId":3,
        "contactId":2,
        "enrolmentId":3,
        "studentName":"stud3",
        "moduleId":1,
        "startDate":"2017-05-15",
        "endDate":"2017-05-25",
        "reportableHours":15.00,
        "deliveryMode":"Online",
        "fundingSource":"State - specific",
        "status":"Withdrawn (40)",
        "hoursAttended":22,
        "specificProgramIdentifier":"zxcvb"
        }
        """

        Given path ishPath + '/99999'
        And request outcomeToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."