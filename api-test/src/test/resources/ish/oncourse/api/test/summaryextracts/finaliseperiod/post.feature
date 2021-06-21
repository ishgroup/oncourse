@parallel=false
Feature: Main feature for all POST requests with path 'summaryextracts/finaliseperiod'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'summaryextracts/finaliseperiod'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Finalise period by admin

        Given path ishPath + '/2016-01-02'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response ==
        """
        {
        "lastDate":"2016-01-03",
        "targetDate":"2016-01-31",
        "unreconciledPaymentsCount":null,
        "unreconciledPaymentsBankingIds":[],
        "unbankedPaymentInCount":null,
        "unbankedPaymentInIds":[],
        "unbankedPaymentOutCount":null,
        "unbankedPaymentOutIds":[],
        "depositBankingCount":null,
        "depositBankingIds":[]
        }
        """


    Scenario: (+) Finalise period by notadmin with access rights

#       <--->  Login as notadmin:
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath + '/2016-01-03'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 204

#       <---> Assertion:
        Given path ishPath
        When method GET
        Then status 200
        And match response ==
        """
        {
        "lastDate":"2016-01-04",
        "targetDate":"2016-01-31",
        "unreconciledPaymentsCount":null,
        "unreconciledPaymentsBankingIds":[],
        "unbankedPaymentInCount":null,
        "unbankedPaymentInIds":[],
        "unbankedPaymentOutCount":null,
        "unbankedPaymentOutIds":[],
        "depositBankingCount":null,
        "depositBankingIds":[]
        }
        """



    Scenario: (-) Finalise period by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#        <--->

        Given path ishPath + '/2016-01-04'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions for Finalise period"



    Scenario: (-) Finalise period by date in the past

        Given path ishPath + '/2016-01-01'
        And request {"headers":{},"params":{},"responseType":""}
        When method POST
        Then status 400
        And match $.errorMessage == "Finalise date must be after or equal the from date."
