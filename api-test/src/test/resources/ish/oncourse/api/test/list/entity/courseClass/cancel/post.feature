
@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/cancel'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/cancel'
        * def ishPathCourseClass = 'list/entity/courseClass'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (-) Cancel more then one class

        Given path ishPath
        And request {"classIds":[10,11],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 400
        And match $.errorMessage == "Can not cancel more then one class"



    Scenario: (+) Cancel CourseClass by admin

        Given path ishPath
        And request {"classIds":[10],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 204

#       <--->  Assertion:
        Given path ishPathCourseClass + '/10'
        When method GET
        Then status 200
        And match $.isCancelled == true



    Scenario: (+) Cancel CourseClass by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsDelete', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath
        And request {"classIds":[12],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 204

#       <--->  Assertion:
        Given path ishPathCourseClass + '/12'
        When method GET
        Then status 200
        And match $.isCancelled == true



    Scenario: (-) Cancel CourseClass by notadmin without access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsView', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath
        And request {"classIds":[13],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to cancel class. Please contact your administrator"



    Scenario: (-) Cancel already cancelled CourseClass by admin

        Given path ishPath
        And request {"classIds":[10],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 400
        And match $.errorMessage == "Class with id=10 was already cancelled."



    Scenario: (-) Cancel CourseClass with enrolment which linked to more than one invoice

        Given path ishPath
        And request {"classIds":[1],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 400
        And match $.errorMessage == "You cannot cancel course1-1 because it has enrolment for stud1  which linked to more than one invoice. Instead cancel the enrolment separately (through Enrolments list), review the credit notes and then process the class cancellation."



    Scenario: (-) Cancel not existing CourseClass

        Given path ishPath
        And request {"classIds":[99999],"refundManualInvoices":true,"sendEmail":true}
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
