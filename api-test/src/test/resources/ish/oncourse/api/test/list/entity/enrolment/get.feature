@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/enrolment'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/enrolment'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all Enrolments by admin

        Given path ishPathList
        And param entity = 'Enrolment'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","101","102","103","104","105","106","107"]



    Scenario: (+) Get list of all Enrolments by notadmin with access rights

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

        Given path ishPathList
        And param entity = 'Enrolment'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1","2","3","4","5","6","101","102","103","104","105","106","107"]



    Scenario: (+) Get Enrolment id=1 by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1,
        "tags":[{"id":236,"name":"enrolments 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "studentContactId":2,
        "studentName":"stud1",
        "courseClassId":1,
        "courseClassName":"Course1 course1-1",
        "confirmationStatus":"Do not send",
        "eligibilityExemptionIndicator":false,
        "outcomeIdTrainingOrg":null,
        "studentIndustryANZSICCode":null,
        "vetClientID":null,
        "vetFundingSourceStateID":null,
        "vetIsFullTime":false,
        "vetTrainingContractID":null,
        "status":"Active",
        "displayStatus":"Complete",
        "source":"office",
        "relatedFundingSourceId":3,
        "studyReason":"Not stated",
        "vetFeeExemptionType":"Not set",
        "fundingSource":"Domestic full fee paying student",
        "associatedCourseIdentifier":null,
        "vetInSchools":null,
        "suppressAvetmissExport":false,
        "vetPurchasingContractID":null,
        "cricosConfirmation":null,
        "vetFeeIndicator":false,
        "trainingPlanDeveloped":null,
        "feeCharged":500.00,
        "feeHelpAmount":0.00,
        "invoicesCount":2,
        "outcomesCount":#number,
        "feeStatus":null,
        "attendanceType":"No information",
        "creditOfferedValue":null,
        "creditUsedValue":null,
        "creditFOEId":null,
        "creditProvider":null,
        "creditProviderType":null,
        "creditTotal":null,
        "creditType":null,
        "creditLevel":null,
        "documents":[],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "assessments":[{"modifiedOn":"#ignore","code":"code1","documents":[],"name":"assessment 1","active":true,"description":"some description","id":1000,"createdOn":"#ignore","tags":[],"gradingTypeId":1}],
        "submissions":[],
        "feeHelpClass":false
        }
        """



    Scenario: (-) Get not existing Enrolment

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Get Enrolment by notadmin with access rights

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

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":1,
        "tags":[{"id":236,"name":"enrolments 1","status":null,"system":null,"urlPath":null,"content":null,"color":null,"weight":null,"taggedRecordsCount":null,"childrenCount":null,"created":null,"modified":null,"requirements":[],"childTags":[]}],
        "studentContactId":2,
        "studentName":"stud1",
        "courseClassId":1,
        "courseClassName":"Course1 course1-1",
        "confirmationStatus":"Do not send",
        "eligibilityExemptionIndicator":false,
        "outcomeIdTrainingOrg":null,
        "studentIndustryANZSICCode":null,
        "vetClientID":null,
        "vetFundingSourceStateID":null,
        "vetIsFullTime":false,
        "vetTrainingContractID":null,
        "status":"Active",
        "displayStatus":"Complete",
        "source":"office",
        "relatedFundingSourceId":3,
        "studyReason":"Not stated",
        "vetFeeExemptionType":"Not set",
        "fundingSource":"Domestic full fee paying student",
        "associatedCourseIdentifier":null,
        "vetInSchools":null,
        "suppressAvetmissExport":false,
        "vetPurchasingContractID":null,
        "cricosConfirmation":null,
        "vetFeeIndicator":false,
        "trainingPlanDeveloped":null,
        "feeCharged":500.00,
        "feeHelpAmount":0.00,
        "invoicesCount":#number,
        "outcomesCount":1,
        "feeStatus":null,
        "attendanceType":"No information",
        "creditOfferedValue":null,
        "creditUsedValue":null,
        "creditFOEId":null,
        "creditProvider":null,
        "creditProviderType":null,
        "creditTotal":null,
        "creditType":null,
        "creditLevel":null,
        "documents":[],
        "customFields":{},
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "assessments":[{"modifiedOn":"#ignore","code":"code1","documents":[],"name":"assessment 1","active":true,"description":"some description","id":1000,"createdOn":"#ignore","tags":[],"gradingTypeId":1}],
        "submissions":[],
        "feeHelpClass":false
        }
        """


    Scenario: (-) Get list of all Enrolments by notadmin without access rights

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

        Given path ishPathList
        And param entity = 'Enrolment'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"



    Scenario: (-) Get Enrolment by notadmin without access rights

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

        Given path ishPath + '/5'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get enrolment. Please contact your administrator"
