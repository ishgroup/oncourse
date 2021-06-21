@parallel=false
Feature: Main feature for all GET requests with path 'tag'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
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
        * configure headers = { Authorization:  'UserWithRightsDelete'}

        
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
        * configure headers = { Authorization:  'UserWithRightsCreate'}

        
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
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
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
        * configure headers = { Authorization:  'UserWithRightsPrint'}

        
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
        * configure headers = { Authorization:  'UserWithRightsView'}

        
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
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
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