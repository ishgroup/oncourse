@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/attendance/tutor'

    Background: Authorize first
        * call read('../../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/attendance/tutor'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass tutor attendance by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":1,"sessionId":1,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":7,"sessionId":2,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":8,"sessionId":3,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":10,"sessionId":4,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":9,"sessionId":5,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":3,"sessionId":6,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":2,"sessionId":7,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":4,"sessionId":8,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":6,"sessionId":9,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Rejected for payroll","note":null,"durationMinutes":null,"hasPayslip":true},
        {"id":5,"sessionId":10,"courseClassTutorId":1,"contactName":"tutor1","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":true}
        ]
        """



    Scenario: (+) Get CourseClass tutor attendance by notadmin

#       <--->  Login as notadmin
        Given path '/logout'
        And request {}
        When method PUT
        * def loginBody = {login: 'UserWithRightsHide', password: 'password', kickOut: 'true', skipTfa: 'true'}

        Given path '/login'
        And request loginBody
        When method PUT
        Then status 200
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":32,"sessionId":39,"courseClassTutorId":6,"contactName":"tutor3","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":31,"sessionId":40,"courseClassTutorId":6,"contactName":"tutor3","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":33,"sessionId":41,"courseClassTutorId":6,"contactName":"tutor3","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":35,"sessionId":42,"courseClassTutorId":6,"contactName":"tutor3","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false},
        {"id":34,"sessionId":43,"courseClassTutorId":6,"contactName":"tutor3","attendanceType":"Not confirmed for payroll","note":null,"durationMinutes":null,"hasPayslip":false}
        ]
        """



    Scenario: (-) Get not existing CourseClass tutor attendance

        Given path ishPath + '/99999'
        When method GET
        Then status 200
        And match $ == []
