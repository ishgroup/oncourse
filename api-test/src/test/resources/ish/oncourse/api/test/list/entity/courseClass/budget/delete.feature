@parallel=false
Feature: Main feature for all DELETE requests with path 'list/entity/courseClass/budget'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/budget'
        * def ishPathClass = 'list/entity/courseClass'
        * def ishPathTutor = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Delete Bugget for Class by admin

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget10","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

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
        * def newExpence = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"delExpense1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"delIncome1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"DelWageForTutor1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def expenseId = get[0] response[?(@.description == 'delExpense1')].id
        * print "expenseId = " + expenseId

        * def incomeId = get[0] response[?(@.description == 'delIncome1')].id
        * print "incomeId = " + incomeId

        * def tutorWageId = get[0] response[?(@.description == 'DelWageForTutor1')].id
        * print "tutorWageId = " + tutorWageId
#       <--->

        Given path ishPath + '/' + expenseId
        When method DELETE
        Then status 204

        Given path ishPath + '/' + incomeId
        When method DELETE
        Then status 204

        Given path ishPath + '/' + tutorWageId
        When method DELETE
        Then status 204

#       <---> Verification (only default line should remain):
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
            """
            [
            {"id":"#number","courseClassid":"#(~~classId)","taxId":1,"accountId":7,"invoiceId":null,"description":"Student enrolment fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":0.00,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}
            ]
            """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (+) Delete Bugget for Class by notadmin with access rights

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget11","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

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
        * def newExpence = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"delExpense2","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"delIncome2","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"DelWageForTutor2","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def expenseId = get[0] response[?(@.description == 'delExpense2')].id
        * print "expenseId = " + expenseId

        * def incomeId = get[0] response[?(@.description == 'delIncome2')].id
        * print "incomeId = " + incomeId

        * def tutorWageId = get[0] response[?(@.description == 'DelWageForTutor2')].id
        * print "tutorWageId = " + tutorWageId

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

        Given path ishPath + '/' + expenseId
        When method DELETE
        Then status 204

        Given path ishPath + '/' + incomeId
        When method DELETE
        Then status 204

        Given path ishPath + '/' + tutorWageId
        When method DELETE
        Then status 204

#       <---> Verification (only default line should remain):
        Given path ishPath + '/' + classId
        When method GET
        Then status 200
        And match $ ==
            """
            [
            {"id":"#number","courseClassid":"#(~~classId)","taxId":1,"accountId":7,"invoiceId":null,"description":"Student enrolment fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":0.00,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}
            ]
            """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Delete Bugget for Class by notadmin without access rights

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget12","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

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
        * def newExpence = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"delExpense3","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"delIncome3","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~classId)","taxId":1,"accountId":null,"invoiceId":null,"description":"DelWageForTutor3","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

        Given path ishPath + '/' + classId
        When method GET
        Then status 200

        * def expenseId = get[0] response[?(@.description == 'delExpense3')].id
        * print "expenseId = " + expenseId

        * def incomeId = get[0] response[?(@.description == 'delIncome3')].id
        * print "incomeId = " + incomeId

        * def tutorWageId = get[0] response[?(@.description == 'DelWageForTutor3')].id
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

        Given path ishPath + '/' + expenseId
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete class badget item. Please contact your administrator"

        Given path ishPath + '/' + incomeId
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete class badget item. Please contact your administrator"

        Given path ishPath + '/' + tutorWageId
        When method DELETE
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to delete class badget item. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * def loginBody = {login: 'admin', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200

        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204



    Scenario: (-) Delete not existing Bugget for Class

        #       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget13","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

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

        Given path ishPath + '/99999'
        When method DELETE
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + classId
        When method DELETE
        Then status 204