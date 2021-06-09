@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/priorLearning'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/priorLearning'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * def ishPathPlain = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get list of all PriorLearnings by admin

        Given path ishPathList
        And param entity = 'PriorLearning'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001","1002"]



    Scenario: (+) Get PriorLearning by admin

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"get1","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["get1"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def outcomeId = get[0] response.outcomes[0].id
        * print "outcomeId = " + outcomeId
#       <--->

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "title":"get1",
        "externalReference":"qwerty",
        "qualificationId":3,
        "qualificationNationalCode":"10218NAT",
        "qualificationLevel":"Certificate I in",
        "qualificationName":"Certificate I in Aboriginal Language/s v2",
        "outcomes":[{"id":"#number","contactId":18,"enrolmentId":null,"studentName":"stud12","moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","trainingPlanStartDate":null,"startDate":"2020-02-01","startDateOverridden":true,"actualStartDate":null,"actualEndDate":null,"trainingPlanEndDate":null,"endDate":"2020-02-18","endDateOverridden":true,"reportableHours":30.0,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":true,"hasCertificate":false,"printed":false,"createdOn":"#ignore","modifiedOn":"#ignore"}],
        "documents":"#ignore",
        "notes":"some notes",
        "contactId":18,
        "contactName":"stud12",
        "outcomeIdTrainingOrg":"123"
        }
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Get not existing PriorLearning

        Given path ishPath + "/99999"
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

    Scenario: (+) Get PriorLearning by notadmin

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"get2","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathPlain
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["get2"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def outcomeId = get[0] response.outcomes[0].id
        * print "outcomeId = " + outcomeId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        {
        "id":"#(~~id)",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "title":"get2",
        "externalReference":"qwerty",
        "qualificationId":3,
        "qualificationNationalCode":"10218NAT",
        "qualificationLevel":"Certificate I in",
        "qualificationName":"Certificate I in Aboriginal Language/s v2",
        "outcomes":[{"id":"#number","contactId":18,"enrolmentId":null,"studentName":"stud12","moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","trainingPlanStartDate":null,"startDate":"2020-02-01","startDateOverridden":true,"actualStartDate":null,"actualEndDate":null,"trainingPlanEndDate":null,"endDate":"2020-02-18","endDateOverridden":true,"reportableHours":30.0,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":true,"hasCertificate":false,"printed":false,"createdOn":"#ignore","modifiedOn":"#ignore"}],
        "documents":"#ignore",
        "notes":"some notes",
        "contactId":18,
        "contactName":"stud12",
        "outcomeIdTrainingOrg":"123"
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



    Scenario: (+) Get list of all PriorLearnings by notadmin with access rights

#       <--->  Login as notadmin
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
        And param entity = 'PriorLearning'
        When method GET
        Then status 200
        And match $.rows[*].id contains ["1001","1002"]



    Scenario: (-) Get list of all PriorLearnings by notadmin without access rights

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
        And param entity = 'PriorLearning'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"
