
@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/enrolment/cancel'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/enrolment/cancel'
        * def ishPathEnrolment = 'list/entity/enrolment'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Cancel Enrolment by admin

        Given path ishPath
        And request {"enrolmentIds":"108","deleteNotSetOutcomes":true,"transfer":false,"invoiceLineParam":[{"invoiceLineId":121,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1},{"invoiceLineId":122,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1},{"invoiceLineId":123,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1}]}
        When method POST
        Then status 204

#       <--->  Assertion:
        Given path ishPathEnrolment + '/108'
        When method GET
        Then status 200
        And match $.status == "Credited"



    Scenario: (-) Cancel already cancelled Enrolment by admin

        Given path ishPath
        And request {"enrolmentIds":"108","deleteNotSetOutcomes":true,"transfer":false,"invoiceLineParam":[{"invoiceLineId":121,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1},{"invoiceLineId":122,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1},{"invoiceLineId":123,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1}]}
        When method POST
        Then status 400
        And match $.errorMessage == "The selected enrolment was already cancelled or refunded."



    Scenario: (-) Cancel not existing Enrolment

        Given path ishPath
        And request {"enrolmentIds":"99999","deleteNotSetOutcomes":true,"transfer":false,"invoiceLineParam":[{"invoiceLineId":121,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1},{"invoiceLineId":122,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1},{"invoiceLineId":123,"accountId":7,"cancellationFee":11,"sendInvoice":true,"taxId":1}]}
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."



    Scenario: (+) Cancel Enrolment by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath
        And request {"enrolmentIds":"113","deleteNotSetOutcomes":true,"transfer":false,"invoiceLineParam":[]}
        When method POST
        Then status 204

#       <--->  Assertion:
        Given path ishPathEnrolment + '/113'
        When method GET
        Then status 200
        And match $.status == "Cancelled"



    Scenario: (-) Cancel Enrolment by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPath
        And request {}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to cancel enrolment. Please contact your administrator"
