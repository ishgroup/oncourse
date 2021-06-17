@parallel=false
Feature: Main feature for all PUT requests with path 'list/option/payroll'

    Background: Authorize first
        * callonce read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/option/payroll'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get wages by admin on 2017-03-05

        * def getWages = {"untilDate":"2017-03-05","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
        And match response ==
        """
        {
        "totalWagesCount":"#number",
        "unprocessedWagesCount":"#number",
        "unconfirmedWagesCount":"#number",
        "unconfirmedClassesIds":"#notnull"
        }
        """



    Scenario: (+) Get wages by admin on 2019-03-05

        * def getWages = {"untilDate":"2019-03-05","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
        And match response ==
        """
        {
        "totalWagesCount":"#number",
        "unprocessedWagesCount":"#number",
        "unconfirmedWagesCount":"#number",
        "unconfirmedClassesIds":"#notnull"
        }
        """



    Scenario: (+) Get wages by admin on 2040-03-05

        * def getWages = {"untilDate":"2040-03-05","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
        And match response ==
        """
        {
        "totalWagesCount":"#number",
        "unprocessedWagesCount":"#number",
        "unconfirmedWagesCount":"#number",
        "unconfirmedClassesIds":"#notnull"
        }
        """



    Scenario: (+) Get wages by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
#       <--->

        * def getWages = {"untilDate":"2040-03-05","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 200
        And match response ==
        """
        {
        "totalWagesCount":"#number",
        "unprocessedWagesCount":"#number",
        "unconfirmedWagesCount":"#number",
        "unconfirmedClassesIds":"#notnull"
        }
        """



    Scenario: (-) Get wages by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        * def getWages = {"untilDate":"2040-03-05","entityName":null,"recordIds":null}

        Given path ishPath
        And param entity = 'Payslip'
        And request getWages
        When method PUT
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to generate payroll. Please contact your administrator"
