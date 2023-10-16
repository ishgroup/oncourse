@parallel=false
Feature: Main feature for all GET requests with path 'list/option/message/recipients'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/option/message/recipients'
        * def ishPathLogin = 'login'
        


    Scenario: (+) Get recipients ids to send by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param entity = 'CourseClass'
        And param messageType = 'Sms'
        And param templateId = 2
        And request {"search":"course.courseClasses.id is 1","pageSize":2,"offset":1,"filter":"(course.isTraineeship == false) and ( (startDateTime < tomorrow and endDateTime >= today and isCancelled is false) or (startDateTime >= tomorrow and endDateTime >= tomorrow and isCancelled is false) or (type is DISTANT_LEARNING and isCancelled is false) )","tagGroups":[]}
        When method POST
        Then status 200
        And match $ contains
            """
            {
            "other":{"sendIds":[],"withoutDestinationIds":[],"suppressToSendIds":[]},
            "tutors":{"sendIds":[1],"withoutDestinationIds":[],"suppressToSendIds":[]},
            "withdrawnStudents":{"sendIds":[],"withoutDestinationIds":[],"suppressToSendIds":[]},
            "students":{"sendIds":[],"withoutDestinationIds":[],"suppressToSendIds":[]},
            "activeStudents":{"sendIds":[2,3,4],"withoutDestinationIds":[],"suppressToSendIds":[]}
            }
            """



    Scenario: (-) Get recipients ids to send by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath
        And param entity = 'Enrolment'
        And param messageType = 'Email'
        And request {"search":"firstName contains \"stud\"","pageSize":50,"offset":0,"filter":"","tagGroups":[]}
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to view this entity. Please contact your administrator"
