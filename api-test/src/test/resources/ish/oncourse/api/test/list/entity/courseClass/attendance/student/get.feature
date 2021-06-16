@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/attendance/student'

    Background: Authorize first
        * call read('../../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/attendance/student'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass student attendance by admin

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":1,"sessionId":2,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":2,"sessionId":10,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":5,"sessionId":5,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":6,"sessionId":1,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":7,"sessionId":6,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":8,"sessionId":4,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":9,"sessionId":9,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":10,"sessionId":7,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":31,"sessionId":10,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":32,"sessionId":6,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":34,"sessionId":4,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":35,"sessionId":2,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":36,"sessionId":9,"contactId":3,"contactName":"stud2","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":37,"sessionId":5,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":38,"sessionId":1,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":39,"sessionId":7,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":101,"sessionId":9,"contactId":4,"contactName":"stud3","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":102,"sessionId":4,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":103,"sessionId":5,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":104,"sessionId":6,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":105,"sessionId":8,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":106,"sessionId":2,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":107,"sessionId":7,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":108,"sessionId":10,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":109,"sessionId":1,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":110,"sessionId":3,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null}
        ]
        """



    Scenario: (+) Get CourseClass student attendance by notadmin

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
        {"id":136,"sessionId":43,"contactId":10,"contactName":"stud4","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":137,"sessionId":41,"contactId":10,"contactName":"stud4","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":138,"sessionId":42,"contactId":10,"contactName":"stud4","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":139,"sessionId":39,"contactId":10,"contactName":"stud4","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":140,"sessionId":40,"contactId":10,"contactName":"stud4","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":141,"sessionId":40,"contactId":19,"contactName":"student1 refund","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":142,"sessionId":43,"contactId":19,"contactName":"student1 refund","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":143,"sessionId":39,"contactId":19,"contactName":"student1 refund","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":144,"sessionId":41,"contactId":19,"contactName":"student1 refund","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":145,"sessionId":42,"contactId":19,"contactName":"student1 refund","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":146,"sessionId":42,"contactId":20,"contactName":"student1 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":147,"sessionId":41,"contactId":20,"contactName":"student1 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":148,"sessionId":40,"contactId":20,"contactName":"student1 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":149,"sessionId":39,"contactId":20,"contactName":"student1 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":150,"sessionId":43,"contactId":20,"contactName":"student1 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":158,"sessionId":43,"contactId":29,"contactName":"student2 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":159,"sessionId":39,"contactId":29,"contactName":"student2 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":160,"sessionId":40,"contactId":29,"contactName":"student2 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":161,"sessionId":41,"contactId":29,"contactName":"student2 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":162,"sessionId":42,"contactId":29,"contactName":"student2 PaymentOut","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null}
        ]
        """



    Scenario: (-) Get not existing CourseClass student attendance

        Given path ishPath + '/99999'
        When method GET
        Then status 200
        And match $ == []
