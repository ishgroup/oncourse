@parallel=false
Feature: Main feature for all PUT requests with path 'user/preference/category'

    Background: Authorize first
        * call read('../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        
        * def ishPath = 'user/preference/category'
        * def ishPathLogin = 'login'



    Scenario: (+) Update favorites categories by admin

        * def updatedCategories = ["Accounts","Applications","Assessment tasks","Audit Logging","Automation","Banking Deposits","Certificates","Change my password","Classes","Community support","Companies","Contacts","Corporate Pass","Courses","Deposit banking","Discounts","Documentation","Documents","Enrolments","Export AVETMISS 8...","Finalise period","Funding Contract","Invoices","Memberships","Messages","Outcomes","Payments In","Payments Out","Preferences","Products","Qualifications","Release notes","Rooms","Sales","Security","Send support request...","Sites","Student Feedback","Students","Tags","Traineeship Courses","Traineeships","Transactions","Tutor pay","Tutors","Units Of Competency","Voucher Types","Waiting lists","onCourse news"]

        Given path ishPath
        And request updatedCategories
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def list = get response.categories[?(@.favorite == true)].category
        * match list contains ["Accounts","Applications","Assessment tasks","Audit Logging","Automation","Banking Deposits","Certificates","Change my password","Classes","Community support","Companies","Contacts","Corporate Pass","Courses","Deposit banking","Discounts","Documentation","Documents","Enrolments","Export AVETMISS 8...","Finalise period","Funding Contract","Invoices","Memberships","Messages","Outcomes","Payments In","Payments Out","Preferences","Products","Qualifications","Release notes","Rooms","Sales","Security","Send support request...","Sites","Student Feedback","Students","Tags","Traineeship Courses","Traineeships","Transactions","Tutor pay","Tutors","Units Of Competency","Voucher Types","Waiting lists","onCourse news"]

#       <---->  Scenario have been finished. Now change back favorites categories:
        * def defaultCategories = []

        Given path ishPath
        And request defaultCategories
        When method PUT
        Then status 204




    Scenario: (+) Update favorites categories by notadmin

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

        * def updatedCategories = ["Assessment tasks","Automation","Change my password","Classes","Community support","Companies","Contacts","Courses","Documentation","Documents","Memberships","Messages","Products","Qualifications","Release notes","Rooms","Sales","Send support request...","Sites","Students","Tags","Traineeship Courses","Traineeships","Tutors","Units Of Competency","Voucher Types","onCourse news"]

        Given path ishPath
        And request updatedCategories
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def list = get response.categories[?(@.favorite == true)].category
        * match list contains ["Assessment tasks","Automation","Change my password","Classes","Community support","Companies","Contacts","Courses","Documentation","Documents","Memberships","Messages","Products","Qualifications","Release notes","Rooms","Sales","Send support request...","Sites","Students","Tags","Traineeship Courses","Traineeships","Tutors","Units Of Competency","Voucher Types","onCourse news"]

#       <---->  Scenario have been finished. Now change back favorites categories:
        * def defaultCategories = []

        Given path ishPath
        And request defaultCategories
        When method PUT
        Then status 204

        Given path ishPath
        When method GET
        Then status 200

        * def list = get response[?(@.favorite == true)].category
        * match list contains []

