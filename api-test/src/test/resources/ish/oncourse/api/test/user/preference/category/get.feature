@parallel=false
Feature: Main feature for all GET requests with path 'user/preference/category'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/preference/category'
        * def ishPathLogin = 'login'



    Scenario: (+) Get categories by admin

        Given path ishPath
        When method GET
        Then status 200
        And match $.categories[*].category contains
        """
        [
        "Checkout (Quick Enrol)",
        "Accounts",
        "Applications",
        "Assessment tasks",
        "Audit Logging",
        "Automation",
        "Banking Deposits",
        "Certificates",
        "Change my password",
        "Classes",
        "Community support",
        "Companies",
        "Contacts",
        "Corporate Pass",
        "Courses",
        "Deposit banking",
        "Discounts",
        "Documentation",
        "Documents",
        "Enrolments",
        "Export AVETMISS 8...",
        "Finalise period",
        "Funding Contract",
        "Invoices",
        "Memberships",
        "Messages",
        "Outcomes",
        "Payments In",
        "Payments Out",
        "Preferences",
        "Products",
        "Qualifications",
        "Release notes",
        "Rooms",
        "Sales",
        "Security",
        "Send support request...",
        "Sites",
        "Student Feedback",
        "Students",
        "Tags",
        "Traineeship Courses",
        "Traineeships",
        "Transactions",
        "Tutor pay",
        "Tutors",
        "Units Of Competency",
        "Voucher Types",
        "Waiting lists",
        "onCourse news"
        ]
        """


    Scenario: (+) Get Get categories by notadmin

#       <---> Login as notadmin
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
        And match $.categories[*].category contains
        """
        [
        "Assessment tasks",
        "Automation",
        "Change my password",
        "Classes",
        "Community support",
        "Companies",
        "Contacts",
        "Courses",
        "Documentation",
        "Documents",
        "Memberships",
        "Messages",
        "Products",
        "Qualifications",
        "Release notes",
        "Rooms",
        "Sales",
        "Send support request...",
        "Sites",
        "Students",
        "Tags",
        "Traineeship Courses",
        "Traineeships",
        "Tutors",
        "Units Of Competency",
        "Voucher Types",
        "onCourse news"
        ]
        """