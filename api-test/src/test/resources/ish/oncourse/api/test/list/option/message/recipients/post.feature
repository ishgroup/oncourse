@parallel=false
Feature: Main feature for all GET requests with path 'list/option/message/recipients'

    Background: Authorize first
        * callonce read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/option/message/recipients'
        * def ishPathLogin = 'login'
        


    Scenario: (+) Get recipients ids to send by notadmin with access rights

#       <--->  Login as notadmin
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
        And param entity = 'CourseClass'
        And param messageType = 'Sms'
        And request {"search":"course.courseClasses.id is 1","pageSize":2,"offset":1,"filter":"(course.isTraineeship == false) and ( (startDateTime < tomorrow and endDateTime >= today and isCancelled is false) or (startDateTime >= tomorrow and endDateTime >= tomorrow and isCancelled is false) or (isDistantLearningCourse is true and isCancelled is false) )","tagGroups":[]}
        When method POST
        Then status 200
        And match $ ==
            """
            {
            "other":{"sendSize":0,"withoutDestinationSize":0,"suppressToSendSize":0},
            "tutors":{"sendSize":1,"withoutDestinationSize":0,"suppressToSendSize":0},
            "withdrawnStudents":{"sendSize":0,"withoutDestinationSize":0,"suppressToSendSize":0},
            "students":{"sendSize":0,"withoutDestinationSize":0,"suppressToSendSize":0},
            "activeStudents":{"sendSize":3,"withoutDestinationSize":0,"suppressToSendSize":0}
            }
            """



    Scenario: (-) Get recipients ids to send by notadmin without access rights

#       <--->  Login as notadmin
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
        And param entity = 'Enrolment'
        And param messageType = 'Email'
        And request {"search":"firstName contains \"stud\"","pageSize":50,"offset":0,"filter":"","tagGroups":[]}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"
