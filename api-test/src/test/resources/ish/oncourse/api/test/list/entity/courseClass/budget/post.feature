@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/budget'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/budget'
        * def ishPathClass = 'list/entity/courseClass'
        * def ishPathTutor = 'list/entity/courseClass/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Create Bugget for Class by admin

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget1","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def id = response
        * print "id = " + id

        * def newTutor = {"id":null,"classId":"#(~~id)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId
#       <--->

        # Add Expense:
        * def newExpence = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"expense1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"income1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":7,"invoiceId":null,"description":"Student enrolment fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":0.00,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"expense1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800.00,"perUnitAmountIncTax":880.00,"actualAmount":800.00,"unitCount":null,"contactId":15,"contactName":"stud9","flowType":"Expense","repetitionType":"Fixed","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"income1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600.00,"perUnitAmountIncTax":660.00,"actualAmount":600.00,"unitCount":null,"contactId":14,"contactName":"stud8","flowType":"Income","repetitionType":"Fixed","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200.00,"perUnitAmountIncTax":null,"actualAmount":200.00,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}
        ]
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + id
        When method DELETE
        Then status 204



    Scenario: (+) Create Bugget for Class by notadmin with access rights

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget2","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def id = response
        * print "id = " + id

        * def newTutor = {"id":null,"classId":"#(~~id)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        # Add Expense:
        * def newExpence = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"expense1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 204

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"income1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 204

        # Add Wage for tutor:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        

        * def newWage = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 204

#       <---> Verification:
        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":7,"invoiceId":null,"description":"Student enrolment fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":0.00,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"expense1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800.00,"perUnitAmountIncTax":880.00,"actualAmount":800.00,"unitCount":null,"contactId":15,"contactName":"stud9","flowType":"Expense","repetitionType":"Fixed","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"income1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600.00,"perUnitAmountIncTax":660.00,"actualAmount":600.00,"unitCount":null,"contactId":14,"contactName":"stud8","flowType":"Income","repetitionType":"Fixed","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":"#number","courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor1","invoiceToStudent":false,"payableOnEnrolment":false,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200.00,"perUnitAmountIncTax":null,"actualAmount":200.00,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)"}
        ]
        """

#       <--->  Scenario have been finished. Now remove created object from DB:
        Given path ishPathClass + '/' + id
        When method DELETE
        Then status 204



    Scenario: (-) Create Bugget for Class by notadmin without access rights

#       <---> Add new class and tutor for testing:
        * def newClass = {"id":null,"code":"budget3","courseId":4,"courseCode":"course4","courseName":"Course4","endDateTime":null,"startDateTime":"2020-02-17T15:14:01.445Z","attendanceType":"No information","deliveryMode":"Classroom","fundingSource":"State - specific","budgetedPlaces":0,"censusDate":null,"createdOn":null,"modifiedOn":null,"deposit":null,"detBookingId":null,"expectedHours":null,"feeExcludeGST":null,"feeHelpClass":false,"finalDetExport":null,"initialDetExport":null,"isActive":true,"isCancelled":false,"isDistantLearningCourse":false,"isShownOnWeb":false,"maxStudentAge":null,"maximumDays":null,"maximumPlaces":999,"message":null,"midwayDetExport":null,"minStudentAge":null,"minimumPlaces":1,"reportableHours":12,"sessionsCount":null,"suppressAvetmissExport":false,"vetCourseSiteID":null,"vetFundingSourceStateID":null,"vetPurchasingContractID":null,"vetPurchasingContractScheduleID":null,"webDescription":null,"relatedFundingSourceId":null,"roomId":null,"taxId":null,"summaryFee":null,"summaryDiscounts":null,"enrolmentsToProfitLeftCount":null,"successAndQueuedEnrolmentsCount":null,"tags":[],"documents":[],"isTraineeship":false}

        Given path ishPathClass
        And request newClass
        When method POST
        Then status 200

        * def id = response
        * print "id = " + id

        * def newTutor = {"id":null,"classId":"#(~~id)","contactId":1,"roleId":3,"tutorName":"tutor1 tutor1","roleName":"Coordinator","confirmedOn":null,"isInPublicity":true}

        Given path ishPathTutor
        And request newTutor
        When method POST
        Then status 200

        * def courseClassTutorId = response
        * print "courseClassTutorId = " + courseClassTutorId

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        # Add Expense:
        * def newExpence = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"expense1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":800,"perUnitAmountIncTax":880,"actualAmount":0,"unitCount":null,"contactId":15,"contactName":null,"flowType":"Expense","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newExpence
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create class badget item. Please contact your administrator"

        # Add Income:
        * def newIncome = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"income1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":600,"perUnitAmountIncTax":660,"actualAmount":0,"unitCount":null,"contactId":14,"contactName":null,"flowType":"Income","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null}

        Given path ishPath
        And request newIncome
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create class badget item. Please contact your administrator"

        # Add Wage for tutor:
        * def newWage = {"id":null,"courseClassid":"#(~~id)","taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor1","invoiceToStudent":null,"payableOnEnrolment":null,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200,"perUnitAmountIncTax":0,"actualAmount":0,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Fixed","courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":"#(~~courseClassTutorId)","isOverriden":true}

        Given path ishPath
        And request newWage
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to create class badget item. Please contact your administrator"

#       <--->  Scenario have been finished. Now remove created object from DB:
        * configure headers = { Authorization: 'admin'}

        

        Given path ishPathClass + '/' + id
        When method DELETE
        Then status 204