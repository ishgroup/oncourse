@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/attendance/student'

    Background: Authorize first
        * call read('../../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/attendance/student'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        


        * def attendanceToDefault = [{"id":1,"sessionId":2,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":2,"sessionId":10,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":5,"sessionId":5,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":6,"sessionId":1,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},{"id":7,"sessionId":6,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":8,"sessionId":4,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":9,"sessionId":9,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":10,"sessionId":7,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":31,"sessionId":10,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":32,"sessionId":6,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":34,"sessionId":4,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":35,"sessionId":2,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":36,"sessionId":9,"contactId":3,"contactName":"stud2","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},{"id":37,"sessionId":5,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":38,"sessionId":1,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":39,"sessionId":7,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":101,"sessionId":9,"contactId":4,"contactName":"stud3","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},{"id":102,"sessionId":4,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":103,"sessionId":5,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":104,"sessionId":6,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":105,"sessionId":8,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":106,"sessionId":2,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":107,"sessionId":7,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":108,"sessionId":10,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":109,"sessionId":1,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":110,"sessionId":3,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null}]



    Scenario: (+) Update CourseClass student attendance by admin

        * def attendanceToUpdate =   [{"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"},{"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Absent with reason","note":"reason","attendedFrom":null,"attendedUntil":null},{"id":110,"sessionId":3,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Absent without reason","note":null,"attendedFrom":null,"attendedUntil":null},{"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null}]

        Given path ishPath + '/1'
        And request attendanceToUpdate
        When method POST
        Then status 204

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
        """
        [
        {"id":1,"sessionId":2,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":2,"sessionId":10,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Absent with reason","note":"reason","attendedFrom":null,"attendedUntil":null},
        {"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":5,"sessionId":5,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":6,"sessionId":1,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":7,"sessionId":6,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":8,"sessionId":4,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":9,"sessionId":9,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":10,"sessionId":7,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":31,"sessionId":10,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":32,"sessionId":6,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"},
        {"id":34,"sessionId":4,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":35,"sessionId":2,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":36,"sessionId":9,"contactId":3,"contactName":"stud2","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":37,"sessionId":5,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":38,"sessionId":1,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":39,"sessionId":7,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
        {"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Absent without reason","note":null,"attendedFrom":null,"attendedUntil":null},
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

#       <--->  Scenario have been finished. Now change back object to default:
        Given path ishPath + '/1'
        And request attendanceToDefault
        When method POST
        Then status 204



    Scenario: (+) Update CourseClass student attendance by notadmin with access rights

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

        * def attendanceToUpdate =   [{"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"},{"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Absent with reason","note":"reason","attendedFrom":null,"attendedUntil":null},{"id":110,"sessionId":3,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Absent without reason","note":null,"attendedFrom":null,"attendedUntil":null},{"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null}]

        Given path ishPath + '/1'
        And request attendanceToUpdate
        When method POST
        Then status 204

        Given path ishPath + '/1'
        When method GET
        Then status 200
        And match $ ==
            """
            [
            {"id":1,"sessionId":2,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":2,"sessionId":10,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Absent with reason","note":"reason","attendedFrom":null,"attendedUntil":null},
            {"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":5,"sessionId":5,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":6,"sessionId":1,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":7,"sessionId":6,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":8,"sessionId":4,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":9,"sessionId":9,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":10,"sessionId":7,"contactId":2,"contactName":"stud1","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":31,"sessionId":10,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":32,"sessionId":6,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"},
            {"id":34,"sessionId":4,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":35,"sessionId":2,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":36,"sessionId":9,"contactId":3,"contactName":"stud2","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":37,"sessionId":5,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":38,"sessionId":1,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":39,"sessionId":7,"contactId":3,"contactName":"stud2","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},
            {"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Absent without reason","note":null,"attendedFrom":null,"attendedUntil":null},
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

#       <--->  Scenario have been finished. Now change back object to default:
        Given path ishPath + '/1'
        And request attendanceToDefault
        When method POST
        Then status 204



    Scenario: (-) Update CourseClass student attendance by notadmin without access rights

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

        * def attendanceToUpdate =   [{"id":33,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"},{"id":3,"sessionId":8,"contactId":2,"contactName":"stud1","attendanceType":"Absent with reason","note":"reason","attendedFrom":null,"attendedUntil":null},{"id":110,"sessionId":3,"contactId":4,"contactName":"stud3","attendanceType":"Unmarked","note":null,"attendedFrom":null,"attendedUntil":null},{"id":40,"sessionId":3,"contactId":3,"contactName":"stud2","attendanceType":"Absent without reason","note":null,"attendedFrom":null,"attendedUntil":null},{"id":4,"sessionId":3,"contactId":2,"contactName":"stud1","attendanceType":"Attended","note":null,"attendedFrom":null,"attendedUntil":null}]

        Given path ishPath + '/1'
        And request attendanceToUpdate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit student attendance. Please contact your administrator"



    Scenario: (+) Update not existing CourseClass student attendance

        * def attendanceToUpdate =   [{"id":99999,"sessionId":8,"contactId":3,"contactName":"stud2","attendanceType":"Partial","note":"some note","attendedFrom":"2017-05-06T14:54:31.000Z","attendedUntil":"2017-05-06T15:24:00.000Z"}]

        Given path ishPath + '/99999'
        And request attendanceToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
