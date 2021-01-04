@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/courseClass/trainingPlan'

    Background: Authorize first
        * call read('../../../../signIn.feature')
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/trainingPlan'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        * configure httpClientClass = 'ish.oncourse.api.test.client.KarateClient'



    Scenario: (+) Get CourseClass trainingPlan by admin

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ contains [{"moduleTitle":"Manage personal career goals","assessmentIds":[],"temporarySessionIds":[],"moduleName":"AUM1001A","moduleId":3,"sessionIds":[39,40,41,42],"temporaryAssessmentIds":[]}]



    Scenario: (+) Get CourseClass trainingPlan by notadmin

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
        And match $ contains [{"moduleTitle":"Manage personal career goals","assessmentIds":[],"temporarySessionIds":[],"moduleName":"AUM1001A","moduleId":3,"sessionIds":[39,40,41,42],"temporaryAssessmentIds":[]}]



    Scenario: (-) Get not existing CourseClass trainingPlan

        Given path ishPath + '/99999'
        When method GET
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
