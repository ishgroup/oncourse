@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/priorLearning'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/priorLearning'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list/plain'
        


        
    Scenario: (+) Delete existing PriorLearning by admin

#       <----->  Add a new entity for deleting and get id:
        * def newPriorLearning = {"title":"del1","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
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



    Scenario: (+) Delete existing PriorLearning by notadmin with access rights

#       <----->  Add a new entity for deleting and get id:
        * def newPriorLearning = {"title":"del2","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["del2"])].id
        * print "id = " + id

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204

#       <---> Verification of deleting
        Given path ishPath + '/' + id
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '" + id + "' doesn't exist."



    Scenario: (-) Delete existing PriorLearning by notadmin without access rights

#       <----->  Add a new entity for deleting and get id:
        * def newPriorLearning = {"title":"del3","contactId":18,"externalReference":"qwerty","qualificationId":3,"qualificationLevel":"Certificate I in","qualificationNationalCode":"10218NAT","qualificationName":"Aboriginal Language/s v2","outcomeIdTrainingOrg":"123","notes":"some notes","outcomes":[{"id":null,"contactId":null,"enrolmentId":null,"studentName":null,"moduleId":2,"moduleCode":"AUM1503A","moduleName":"Create new product designs","startDate":"2020-02-01","endDate":"2020-02-18","reportableHours":30,"deliveryMode":"Classroom and online","fundingSource":"State - specific","status":"Competency achieved/pass (20)","hoursAttended":20,"vetPurchasingContractID":"123","vetPurchasingContractScheduleID":"123","vetFundingSourceStateID":"123","specificProgramIdentifier":"123","isPriorLearning":false,"hasCertificate":null,"printed":false,"createdOn":null}],"documents":[{"id":200}]}

        Given path ishPath
        And request newPriorLearning
        When method POST
        Then status 204

        Given path ishPathList
        And param entity = 'PriorLearning'
        And param columns = 'title'
        When method GET
        Then status 200

        * def id = get[0] response.rows[?(@.values == ["del3"])].id
        * print "id = " + id

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/' + id
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete contacts prior learning. Please contact your administrator"

#       <---->  Scenario have been finished. Now delete created entity:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPath + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Delete NOT existing PriorLearning

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match response.errorMessage == "Record with id = '99999' doesn't exist."

