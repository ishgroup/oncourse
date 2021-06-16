@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/attendance/tutor'

    Background: Authorize first
        * call read('../../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/attendance/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        


        * def attendanceToDefault = [{"id":14,"sessionId":20,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":30,"hasPayslip":false},{"id":12,"sessionId":18,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},{"id":17,"sessionId":13,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},{"id":19,"sessionId":15,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":"","durationMinutes":null,"hasPayslip":false}]



    Scenario: (+) Update CourseClass tutor attendance by admin

        * def attendanceToUpdate =   [{"id":14,"sessionId":20,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":30,"hasPayslip":false},{"id":19,"sessionId":15,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":"notes","durationMinutes":null,"hasPayslip":false},{"id":17,"sessionId":13,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Rejected for payroll","note":null,"durationMinutes":null,"hasPayslip":false},{"id":12,"sessionId":18,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false}]

        Given path ishPath + '/2'
        And request attendanceToUpdate
        When method POST
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":16,"sessionId":11,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":11,"sessionId":12,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":17,"sessionId":13,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Rejected for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":15,"sessionId":14,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":19,"sessionId":15,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":"notes","durationMinutes":null,"hasPayslip":false},
        {"id":18,"sessionId":16,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":20,"sessionId":17,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":12,"sessionId":18,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":13,"sessionId":19,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":14,"sessionId":20,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":30,"hasPayslip":false}
        ]
        """

#       <--->  Scenario have been finished. Now change back object to default:
        Given path ishPath + '/2'
        And request attendanceToDefault
        When method POST
        Then status 204



    Scenario: (+) Update CourseClass tutor attendance by notadmin with access rights

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsEdit', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        * def attendanceToUpdate =   [{"id":14,"sessionId":20,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":30,"hasPayslip":false},{"id":19,"sessionId":15,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":"notes","durationMinutes":null,"hasPayslip":false},{"id":17,"sessionId":13,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Rejected for payroll","note":null,"durationMinutes":null,"hasPayslip":false},{"id":12,"sessionId":18,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false}]

        Given path ishPath + '/2'
        And request attendanceToUpdate
        When method POST
        Then status 204

        Given path ishPath + '/2'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":16,"sessionId":11,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":11,"sessionId":12,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":17,"sessionId":13,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Rejected for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":15,"sessionId":14,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":19,"sessionId":15,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":"notes","durationMinutes":null,"hasPayslip":false},
        {"id":18,"sessionId":16,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":20,"sessionId":17,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":12,"sessionId":18,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":13,"sessionId":19,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":14,"sessionId":20,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":30,"hasPayslip":false}
        ]
        """

#       <--->  Scenario have been finished. Now change back object to default:
        Given path ishPath + '/2'
        And request attendanceToDefault
        When method POST
        Then status 204



    Scenario: (-) Update CourseClass tutor attendance by notadmin without access rights

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

        * def attendanceToUpdate =   [{"id":14,"sessionId":20,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":30,"hasPayslip":false},{"id":19,"sessionId":15,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":"notes","durationMinutes":null,"hasPayslip":false},{"id":17,"sessionId":13,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Rejected for payroll","note":null,"durationMinutes":null,"hasPayslip":false},{"id":12,"sessionId":18,"courseClassTutorId":2,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false}]

        Given path ishPath + '/2'
        And request attendanceToUpdate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit tutor attendance. Please contact your administrator"



    Scenario: (-) Update CourseClass tutor attendance with payslip

        * def attendanceToUpdate = [{"id":1,"sessionId":1,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true}]

        Given path ishPath + '/1'
        And request attendanceToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Attendance with linked payslip cannot be changed"



#    Scenario: (+) Update not existing CourseClass tutor attendance
#
#        * def attendanceToUpdate =   [{"id":99999,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"}]
#
#        Given path ishPath + '/99999'
#        And request attendanceToUpdate
#        When method POST
#        Then status 400
#        And match $.errorMessage == "Record with id = '99999' doesn't exist."
