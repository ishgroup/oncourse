@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/trainingPlan'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/trainingPlan'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        



    Scenario: (+) Get CourseClass trainingPlan by admin

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ contains [{"moduleTitle":"Manage personal career goals","assessmentIds":[],"temporarySessionIds":[],"moduleName":"AUM1001A","moduleId":3,"sessionIds":[39,40,41,42],"temporaryAssessmentIds":[]}]



    Scenario: (+) Get CourseClass trainingPlan by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ contains [{"moduleTitle":"Manage personal career goals","assessmentIds":[],"temporarySessionIds":[],"moduleName":"AUM1001A","moduleId":3,"sessionIds":[39,40,41,42],"temporaryAssessmentIds":[]}]



    Scenario: (-) Get not existing CourseClass trainingPlan

        Given path ishPath + '/99999'
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
