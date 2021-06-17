@parallel=false
Feature: Main feature for all GET requests with path 'tag'

    Background: Authorize first
        * call read('../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'tag'
        * def ishPathLogin = 'login'
        



    Scenario: (+) Get list of all tags by admin

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList",
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """



    Scenario: (+) Get list of all tags by notadmin with maximum permissions: Hide-View-Print-Edit-Create-Delete

#       <--->  Login as notadmin with access rights Delete:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList",
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """



    Scenario: (+) Get all tags by notadmin with maximum permissions: Hide-View-Print-Edit-Create

#       <--->  Login as notadmin with access rights Create:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsCreate', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList",
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """



    Scenario: (+) Get list of all tags by notadmin with permissions: Hide-View-Print-Edit

#       <--->  Login as notadmin with access rights Edit:
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

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList"
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """



    Scenario: (+) Get list of all tags by notadmin with permissions: Hide-View-Print

#       <--->  Login as notadmin with access rights Print:
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

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList"
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """



    Scenario: (+) Get list of all tags by notadmin with permissions: Hide-View

#       <--->  Login as notadmin with access rights View:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList"
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """



    Scenario: (+) Get list of all tags by notadmin with minimum permissions: Hide

#       <--->  Login as notadmin with access rights Hide:
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path ishPathLogin
        And request loginBody
        When method PUT
        Then status 200
        And match response.loginStatus == "Login successful"
#       <--->

        Given path ishPath
        When method GET
        Then status 200
        And match karate.sizeOf(response) == 14

        And match response[*].name contains
        """
        [
        "Assessment method",
        "Mailing Lists",
        "Payroll wage intervals",
        "testTag for applications",
        "Subjects",
        "testTag for documents",
        "testTag for payslips",
        "testTag for rooms",
        "testTag for sites",
        "testTag for waitingList"
        "testTag for Courses",
        "testTag for Classes",
        "testTag for contacts",
        "testTag for enrolments"
        ]
        """