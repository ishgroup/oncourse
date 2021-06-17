@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/outcome'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/outcome'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get list of all Outcomes by admin

        Given path ishPathList
        And param entity = 'Outcome'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","101","102","103","104","105","106","107","109"]



    Scenario: (+) Get list of all Outcomes by notadmin

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

        Given path ishPathList
        And param entity = 'Outcome'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","101","102","103","104","105","106","107","109"]



    Scenario: (+) Get Outcome by admin

        Given path ishPath + "/107"
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":107,
        "contactId":10,
        "enrolmentId":107,
        "studentName":"stud4",
        "moduleId":3,
        "moduleCode":"AUM1001A",
        "moduleName":"Manage personal career goals",
        "trainingPlanStartDate":"2027-02-01",
        "startDate":"2027-02-01",
        "startDateOverridden":true,
        "trainingPlanEndDate":"2027-02-04",
        "endDate":"2027-02-04",
        "endDateOverridden":true,
        "reportableHours":12.0,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Satisfactorily completed (81)",
        "hoursAttended":null,
        "vetPurchasingContractID":null,
        "vetPurchasingContractScheduleID":null,
        "vetFundingSourceStateID":null,
        "specificProgramIdentifier":null,
        "isPriorLearning":false,
        "hasCertificate":true,
        "printed":true,
        "progression":{"attended":0,"marked":0,"notMarked":0,"submitted":0,"futureTimetable":4.0,"absent":0,"notReleased":0,"released":0},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "actualStartDate":"2027-02-01",
        "actualEndDate":"2027-02-04"
        }
        """



    Scenario: (+) Get Outcome by notadmin with access rights

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

        Given path ishPath + "/2"
        When method GET
        Then status 200
        And match $ ==
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
        "startDate":"2018-12-01",
        "startDateOverridden":"#ignore",
        "trainingPlanEndDate":"2027-12-01",
        "endDate":"2027-12-01",
        "endDateOverridden":"#ignore",
        "reportableHours":10.0,
        "deliveryMode":"Classroom",
        "fundingSource":"Domestic full fee paying student",
        "status":"Not set",
        "hoursAttended":null,
        "vetPurchasingContractID":null,
        "vetPurchasingContractScheduleID":null,
        "vetFundingSourceStateID":null,
        "specificProgramIdentifier":null,
        "isPriorLearning":false,
        "hasCertificate":false,
        "printed":false,
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "actualStartDate":"2018-12-01",
        "actualEndDate":"2027-12-01",
        "progression":"#ignore"
        }
        """



    Scenario: (-) Get Outcome by notadmin without access rights

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

        Given path ishPath + "/2"
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get outcome. Please contact your administrator"



    Scenario: (-) Get not existing outcome

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
