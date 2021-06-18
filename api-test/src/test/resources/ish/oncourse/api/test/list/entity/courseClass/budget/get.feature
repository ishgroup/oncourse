@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/budget'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/budget'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass budget by admin

        Given path ishPath + '/3'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":5,"courseClassid":3,"taxId":1,"accountId":7,"invoiceId":null,"description":"Student fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":636.36,"perUnitAmountIncTax":700.00,"actualAmount":"#number","unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":6,"courseClassid":3,"taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor1","invoiceToStudent":false,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":20.00,"perUnitAmountIncTax":null,"actualAmount":200.00,"unitCount":null,"contactId":1,"contactName":"tutor1","flowType":"Wages","repetitionType":"Per session","isOverriden":true,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":3},
        {"id":7,"courseClassid":3,"taxId":1,"accountId":null,"invoiceId":null,"description":"discount2","invoiceToStudent":false,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":63.64,"perUnitAmountIncTax":null,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Discount","repetitionType":"Discount","isOverriden":false,"courseClassDiscount":{"discount":{"id":1001,"name":"discount1","discountType":"Percent","rounding":"No Rounding","discountValue":null,"discountPercent":0.100,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":0.10,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":-30,"validTo":null,"validToOffset":5,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null},"forecast":null,"discountOverride":null},"paymentPlan":[],"courseClassTutorId":null},
        {"id":8,"courseClassid":3,"taxId":1,"accountId":null,"invoiceId":null,"description":"discount1","invoiceToStudent":false,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":63.64,"perUnitAmountIncTax":null,"actualAmount":0.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Discount","repetitionType":"Discount","isOverriden":false,"courseClassDiscount":{"discount":{"id":1001,"name":"discount1","discountType":"Percent","rounding":"No Rounding","discountValue":null,"discountPercent":0.100,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":0.10,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":-30,"validTo":null,"validToOffset":5,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null},"forecast":null,"discountOverride":null},"paymentPlan":[],"courseClassTutorId":null},
        {"id":14,"courseClassid":3,"taxId":1,"accountId":null,"invoiceId":null,"description":"discount2","invoiceToStudent":false,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":127.27,"perUnitAmountIncTax":null,"actualAmount":"#number","unitCount":null,"contactId":null,"contactName":null,"flowType":"Discount","repetitionType":"Discount","isOverriden":false,"courseClassDiscount":{"discount":{"id":1002,"name":"discount2","discountType":"Percent","rounding":"No Rounding","discountValue":null,"discountPercent":0.200,"discountMin":null,"discountMax":null,"cosAccount":null,"predictedStudentsPercentage":0.10,"availableOnWeb":null,"code":null,"validFrom":null,"validFromOffset":null,"validTo":null,"validToOffset":null,"hideOnWeb":null,"description":null,"studentEnrolledWithinDays":null,"studentAgeUnder":null,"studentAge":null,"studentPostcode":null,"discountConcessionTypes":[],"discountMemberships":[],"discountCourseClasses":[],"addByDefault":null,"minEnrolments":null,"minValue":null,"corporatePassDiscounts":[],"createdOn":null,"modifiedOn":null,"limitPreviousEnrolment":null},"forecast":null,"discountOverride":null},"paymentPlan":[],"courseClassTutorId":null}
        ]
        """



    Scenario: (+) Get CourseClass budget by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":16,"courseClassid":6,"taxId":1,"accountId":7,"invoiceId":null,"description":"Student fee","invoiceToStudent":true,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":200.00,"perUnitAmountIncTax":220.00,"actualAmount":800.00,"unitCount":null,"contactId":null,"contactName":null,"flowType":"Income","repetitionType":"Per enrolment","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":null},
        {"id":17,"courseClassid":6,"taxId":1,"accountId":null,"invoiceId":null,"description":"Wage for tutor3","invoiceToStudent":false,"payableOnEnrolment":true,"isSunk":false,"maximumCost":null,"minimumCost":null,"onCostRate":null,"perUnitAmountExTax":0.00,"perUnitAmountIncTax":null,"actualAmount":0.00,"unitCount":null,"contactId":6,"contactName":"tutor3","flowType":"Wages","repetitionType":"Per timetabled hour","isOverriden":false,"courseClassDiscount":null,"paymentPlan":[],"courseClassTutorId":6}
        ]
        """



    Scenario: (-) Get CourseClass budget by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to get class budget. Please contact your administrator"



#    Scenario: (-) Get not existing CourseClass budget
#
#        Given path ishPath + '/99999'
#        When method GET
#        Then status 400
#        And match $.errorMessage == "Record with id = '99999' doesn't exist."
