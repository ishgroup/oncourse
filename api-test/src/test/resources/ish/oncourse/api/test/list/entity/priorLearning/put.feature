@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/priorLearning'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/priorLearning'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update PriorLearning by admin

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"put1","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put1"])].id
        * print "id = " + id

        Given path ishPath + '/' + id
        When method GET
        Then status 200

        * def outcomeId = get[0] response.outcomes[0].id
        * print "outcomeId = " + outcomeId
#       <--->

        * def priorLearningToUpdate =
        """
        {
        "id":"#(id)",
        "title":"put1 UPD",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Electrotechnology Electrician",
        "outcomes":[{"id":"#(~~outcomeId)","contactId":18,"enrolmentId":null,"studentName":"stud12","moduleId":4,"moduleCode":"AUM1002A","moduleName":"Select and use tools and equipment in an automotive manufacturing environment","startDate":"2021-02-01","endDate":"2022-02-18","reportableHours":62,"deliveryMode":"WA: Workplace (6)","fundingSource":"International full fee paying student","status":"Satisfactorily completed (81)","hoursAttended":40,"vetPurchasingContractID":"456","vetPurchasingContractScheduleID":"456","vetFundingSourceStateID":"456","specificProgramIdentifier":"456","isPriorLearning":true,"hasCertificate":false,"printed":false}],
        "documents":[{"id":201}],
        "notes":"some notes UPD",
        "contactId":16,
        "outcomeIdTrainingOrg":"456"
        }
        """

        Given path ishPath + '/' + id
        And request priorLearningToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "title":"put1 UPD",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Certificate III in Electrotechnology Electrician",
        "outcomes":[{"id":"#(~~outcomeId)","progression":null,"contactId":16,"enrolmentId":null,"studentName":"stud10","moduleId":4,"moduleCode":"AUM1002A","moduleName":"Select and use tools and equipment in an automotive manufacturing environment","trainingPlanStartDate":null,"startDate":"2021-02-01","startDateOverridden":true,"actualStartDate":null,"actualEndDate":null,"trainingPlanEndDate":null,"endDate":"2022-02-18","endDateOverridden":true,"reportableHours":62.0,"deliveryMode":"WA: Workplace (6)","fundingSource":"International full fee paying student","status":"Satisfactorily completed (81)","hoursAttended":40,"vetPurchasingContractID":"456","vetPurchasingContractScheduleID":"456","vetFundingSourceStateID":"456","specificProgramIdentifier":"456","isPriorLearning":true,"hasCertificate":false,"printed":false,"createdOn":"#ignore","modifiedOn":"#ignore"}],
        "documents":"#ignore",
        "notes":"some notes UPD",
        "contactId":16,
        "contactName":"stud10",
        "outcomeIdTrainingOrg":"456"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update PriorLearning to empty Title

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"put3","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[],"documents":[]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put3"])].id
        * print "id = " + id
#       <--->

        * def priorLearningToUpdate =
        """
        {
        "id":"#(id)",
        "title":"",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Electrotechnology Electrician",
        "outcomes":[],
        "documents":[],
        "notes":"some notes UPD",
        "contactId":16,
        "outcomeIdTrainingOrg":"456"
        }
        """

        Given path ishPath + '/' + id
        And request priorLearningToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "You should provide a title for this prior learning."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update PriorLearning to empty Contact

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"put4","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[],"documents":[]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put4"])].id
        * print "id = " + id
#       <--->

        * def priorLearningToUpdate =
        """
        {
        "id":"#(id)",
        "title":"put4",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Electrotechnology Electrician",
        "outcomes":[],
        "documents":[],
        "notes":"some notes UPD",
        "contactId":null,
        "outcomeIdTrainingOrg":"456"
        }
        """

        Given path ishPath + '/' + id
        And request priorLearningToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "You should add a student."

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update PriorLearning to not existing Contact

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"put5","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[],"documents":[]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put5"])].id
        * print "id = " + id
#       <--->

        * def priorLearningToUpdate =
        """
        {
        "id":"#(id)",
        "title":"put5",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Electrotechnology Electrician",
        "outcomes":[],
        "documents":[],
        "notes":"some notes UPD",
        "contactId":99999,
        "outcomeIdTrainingOrg":"456"
        }
        """

        Given path ishPath + '/' + id
        And request priorLearningToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Contact with id:99999 doesn't exist"

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing PriorLearning

        * def priorLearningToUpdate =
        """
        {
        "id":99999,
        "title":"put6",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Electrotechnology Electrician",
        "outcomes":[],
        "documents":[],
        "notes":"some notes UPD",
        "contactId":16,
        "outcomeIdTrainingOrg":"456"
        }
        """

        Given path ishPath + '/99999'
        And request priorLearningToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Update PriorLearning by notadmin with access rights

#       <----->  Add a new entity to update and define its id:
        * def newPriorLearning = {"title":"put2","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["put2"])].id
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
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        * def priorLearningToUpdate =
        """
        {
        "id":"#(id)",
        "title":"put2 UPD",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Electrotechnology Electrician",
        "outcomes":[{"id":"#(~~outcomeId)","contactId":18,"enrolmentId":null,"studentName":"stud12","moduleId":4,"moduleCode":"AUM1002A","moduleName":"Select and use tools and equipment in an automotive manufacturing environment","startDate":"2021-02-01","endDate":"2022-02-18","reportableHours":62,"deliveryMode":"WA: Workplace (6)","fundingSource":"International full fee paying student","status":"Satisfactorily completed (81)","hoursAttended":40,"vetPurchasingContractID":"456","vetPurchasingContractScheduleID":"456","vetFundingSourceStateID":"456","specificProgramIdentifier":"456","isPriorLearning":true,"hasCertificate":false,"printed":false}],
        "documents":[{"id":201}],
        "notes":"some notes UPD",
        "contactId":16,
        "outcomeIdTrainingOrg":"456"
        }
        """

        Given path ishPath + '/' + id
        And request priorLearningToUpdate
        When method PUT
        Then status 204

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ contains
        """
        {
        "id":"#(~~id)",
        "createdOn":"#ignore",
        "modifiedOn":"#ignore",
        "title":"put2 UPD",
        "externalReference":"qwerty_UPD",
        "qualificationId":1,
        "qualificationNationalCode":"UEE30807",
        "qualificationLevel":"Certificate III in",
        "qualificationName":"Certificate III in Electrotechnology Electrician",
        "outcomes":[{"id":"#(~~outcomeId)","progression":null,"contactId":16,"enrolmentId":null,"studentName":"stud10","moduleId":4,"moduleCode":"AUM1002A","moduleName":"Select and use tools and equipment in an automotive manufacturing environment","trainingPlanStartDate":null,"startDate":"2021-02-01","startDateOverridden":true,"actualStartDate":null,"actualEndDate":null,"trainingPlanEndDate":null,"endDate":"2022-02-18","endDateOverridden":true,"reportableHours":62.0,"deliveryMode":"WA: Workplace (6)","fundingSource":"International full fee paying student","status":"Satisfactorily completed (81)","hoursAttended":40,"vetPurchasingContractID":"456","vetPurchasingContractScheduleID":"456","vetFundingSourceStateID":"456","specificProgramIdentifier":"456","isPriorLearning":true,"hasCertificate":false,"printed":false,"createdOn":"#ignore","modifiedOn":"#ignore"}],
        "documents":"#ignore",
        "notes":"some notes UPD",
        "contactId":16,
        "contactName":"stud10",
        "outcomeIdTrainingOrg":"456"
        }
        """

#       <----->  Scenario have been finished. Now find and remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Update PriorLearning by notadmin without access rights

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

        * def priorLearningToUpdate = {}

        Given path ishPath + '/1001'
        And request priorLearningToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to update contacts prior learning. Please contact your administrator."
