@parallel=false
Feature: Main feature for all GET requests with path 'summaryextracts/finaliseperiod'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'summaryextracts/finaliseperiod'
        * def ishPathLogin = 'login'
        


        
    Scenario: (+) Get Finalise period information by admin

        Given path ishPath
        And param lockDate = '2016-01-31'
        When method GET
        Then status 200
        And match response ==
        """
        {
        "lastDate":"2016-01-01",
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



    Scenario: (+) Get Finalise period information by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        Given path ishPath
        And param lockDate = '2016-01-31'
        When method GET
        Then status 200
        And match response ==
        """
        {
        "lastDate":"2016-01-01",
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



    Scenario: (-) Get Finalise period information by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param lockDate = '2016-01-31'
        When method GET
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions for Finalise period"



    Scenario: (-) Get Finalise period information when lockDate is before than from date

        Given path ishPath
        And param lockDate = '2015-01-31'
        When method GET
        Then status 400
        And match $.errorMessage == "Finalise date must be after or equal the from date."

