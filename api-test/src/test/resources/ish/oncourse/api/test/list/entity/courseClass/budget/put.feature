@parallel=false
Feature: Main feature for all PUT requests with path 'list/entity/courseClass/budget'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/budget'
        * def ishPathClass = 'list/entity/courseClass'
        * def ishPathTutor = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Update Bugget for Class by admin

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget100","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"fullTimeLoad":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def classId = response
        * print "classId = " + classId

        * def newTutor = {"id":null,"classId":"#(~~classId)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId
#       <--->

        # Add Expense:
        * def newExpence = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putExpense1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putIncome1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def expenseId = get[0] response[?(@.description == 'putExpense1')].id
        * print "expenseId = " + expenseId

        * def incomeId = get[0] response[?(@.description == 'putIncome1')].id
        * print "incomeId = " + incomeId

        * def tutorWageId = get[0] response[?(@.description == 'putWageForTutor1')].id
        * print "tutorWageId = " + tutorWageId
#       <--->

        * def expenseToUpdate = {"id":"#(expenseId)","courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putExpense1 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":10000,"minimumCost":1000,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":800,"unitCount":null,"contactId":18,"contactName":"stud9","flowType":"Expense","repetitionType":"Per session","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/' + expenseId
        And request expenseToUpdate
        When method PUT
        Then status 204

        * def incomeToUpdate = {"id":"#(incomeId)","courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putIncome1 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":600,"unitCount":1,"contactId":16,"contactName":"stud8","flowType":"Income","repetitionType":"Per unit","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/' + incomeId
        And request incomeToUpdate
        When method PUT
        Then status 204

        * def tutorWageToUpdate = {"id":"#(tutorWageId)","courseClassid":"#(classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":true,"maximumCost":10000,"minimumCost":100,"onCostRate":0.1,"perUnitAmountExTax":100,"perUnitAmountIncTax":null,"actualAmount":200,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Per session","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}

        Given path ishPath + '/' + tutorWageId
        And request tutorWageToUpdate
        When method PUT
        Then status 204

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
                """
                [
                {"id":"#number","courseClassid":"#(~~classId)","taxId":1,"accountId":7,"invoiceId":null,"description":"Student enrolment fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":0.00,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
                {"id":"#(~~expenseId)","courseClassid":"#(~~classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putExpense1 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":10000.00,"minimumCost":1000.00,"onCostRate":null,"perUnitAmountExTax":1000.00,"perUnitAmountIncTax":1000.00,"actualAmount":1000.00,"unitCount":null,"contactId":18,"contactName":"stud12","flowType":"Expense","repetitionType":"Per session","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
                {"id":"#(~~incomeId)","courseClassid":"#(~~classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putIncome1 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":1000.00,"perUnitAmountIncTax":1000.00,"actualAmount":1000.00,"unitCount":1.0000,"contactId":16,"contactName":"stud10","flowType":"Income","repetitionType":"Per unit","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
                {"id":"#(~~tutorWageId)","courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":true,"maximumCost":10000.00,"minimumCost":100.00,"onCostRate":0.1000,"perUnitAmountExTax":100.00,"perUnitAmountIncTax":null,"actualAmount":100.00,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Per session","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}
                ]
                """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Update Bugget for Class by notadmin with access rights

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget101","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"fullTimeLoad":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def classId = response
        * print "classId = " + classId

        * def newTutor = {"id":null,"classId":"#(~~classId)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId
#       <--->

        # Add Expense:
        * def newExpence = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putExpense2","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putIncome2","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor2","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def expenseId = get[0] response[?(@.description == 'putExpense2')].id
        * print "expenseId = " + expenseId

        * def incomeId = get[0] response[?(@.description == 'putIncome2')].id
        * print "incomeId = " + incomeId

        * def tutorWageId = get[0] response[?(@.description == 'putWageForTutor2')].id
        * print "tutorWageId = " + tutorWageId

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def expenseToUpdate = {"id":"#(expenseId)","courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putExpense2 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":10000,"minimumCost":1000,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":800,"unitCount":null,"contactId":18,"contactName":"stud9","flowType":"Expense","repetitionType":"Per session","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/' + expenseId
        And request expenseToUpdate
        When method PUT
        Then status 204

        * def incomeToUpdate = {"id":"#(incomeId)","courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putIncome2 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":600,"unitCount":1,"contactId":16,"contactName":"stud8","flowType":"Income","repetitionType":"Per unit","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/' + incomeId
        And request incomeToUpdate
        When method PUT
        Then status 204

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

        * def tutorWageToUpdate = {"id":"#(tutorWageId)","courseClassid":"#(classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor2","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":true,"maximumCost":10000,"minimumCost":100,"onCostRate":0.1,"perUnitAmountExTax":100,"perUnitAmountIncTax":null,"actualAmount":200,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Per session","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}

        Given path ishPath + '/' + tutorWageId
        And request tutorWageToUpdate
        When method PUT
        Then status 204

#       <---> Verification:
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
                """
                [
                {"id":"#number","courseClassid":"#(~~classId)","taxId":1,"accountId":7,"invoiceId":null,"description":"Student enrolment fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":0.00,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
                {"id":"#(~~expenseId)","courseClassid":"#(~~classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putExpense2 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":10000.00,"minimumCost":1000.00,"onCostRate":null,"perUnitAmountExTax":1000.00,"perUnitAmountIncTax":1000.00,"actualAmount":1000.00,"unitCount":null,"contactId":18,"contactName":"stud12","flowType":"Expense","repetitionType":"Per session","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
                {"id":"#(~~incomeId)","courseClassid":"#(~~classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putIncome2 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":1000.00,"perUnitAmountIncTax":1000.00,"actualAmount":1000.00,"unitCount":1.0000,"contactId":16,"contactName":"stud10","flowType":"Income","repetitionType":"Per unit","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
                {"id":"#(~~tutorWageId)","courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor2","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":true,"maximumCost":10000.00,"minimumCost":100.00,"onCostRate":0.1000,"perUnitAmountExTax":100.00,"perUnitAmountIncTax":null,"actualAmount":100.00,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Per session","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}
                ]
                """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Update Bugget for Class by notadmin without access rights

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget102","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"fullTimeLoad":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def classId = response
        * print "classId = " + classId

        * def newTutor = {"id":null,"classId":"#(~~classId)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId
#       <--->

        # Add Expense:
        * def newExpence = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putExpense3","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putIncome3","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor3","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def expenseId = get[0] response[?(@.description == 'putExpense3')].id
        * print "expenseId = " + expenseId

        * def incomeId = get[0] response[?(@.description == 'putIncome3')].id
        * print "incomeId = " + incomeId

        * def tutorWageId = get[0] response[?(@.description == 'putWageForTutor3')].id
        * print "tutorWageId = " + tutorWageId

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

        * def expenseToUpdate = {"id":"#(expenseId)","courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putExpense3 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":10000,"minimumCost":1000,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":800,"unitCount":null,"contactId":18,"contactName":"stud9","flowType":"Expense","repetitionType":"Per session","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/' + expenseId
        And request expenseToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit class budget. Please contact your administrator"

        * def incomeToUpdate = {"id":"#(incomeId)","courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putIncome3 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":600,"unitCount":1,"contactId":16,"contactName":"stud8","flowType":"Income","repetitionType":"Per unit","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/' + incomeId
        And request incomeToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit class budget. Please contact your administrator"

        * def tutorWageToUpdate = {"id":"#(tutorWageId)","courseClassid":"#(classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"putWageForTutor3","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":true,"maximumCost":10000,"minimumCost":100,"onCostRate":0.1,"perUnitAmountExTax":100,"perUnitAmountIncTax":null,"actualAmount":200,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Per session","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}

        Given path ishPath + '/' + tutorWageId
        And request tutorWageToUpdate
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit class budget. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Update not existing Bugget for Class

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget103","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"fullTimeLoad":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def classId = response
        * print "classId = " + classId

        * def newTutor = {"id":null,"classId":"#(~~classId)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId
#       <--->

        * def expenseToUpdate = {"id":99999,"courseClassid":"#(classId)","taxId":2,"accountId":null,"invoiceId":null,"description":"putExpense3 upd","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":10000,"minimumCost":1000,"onCostRate":null,"perUnitAmountExTax":1000,"perUnitAmountIncTax":1000,"actualAmount":800,"unitCount":null,"contactId":18,"contactName":"stud9","flowType":"Expense","repetitionType":"Per session","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath + '/99999'
        And request expenseToUpdate
        When method PUT
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204