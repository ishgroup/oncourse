@parallel=false
Feature: Main feature for all POST requests with path 'list/entity/courseClass/trainingPlan'

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/courseClass/trainingPlan'
        * def ishPathLogin = 'login'
        * def ishPathList = 'list'
        


        * def trainingPlanToDefault = [{"moduleId":3,"moduleName":"AUM1001A","sessionIds":[39,40,41,42],"temporarySessionIds":[],"assessmentIds":[],"temporaryAssessmentIds":[]}]



    Scenario: (+) Update CourseClass tutor attendance by admin

        * def trainingPlanToUpdate = [{"moduleId":3,"moduleName":"AUM1001A","sessionIds":[43],"temporarySessionIds":[],"assessmentIds":[],"temporaryAssessmentIds":[]}]

        Given path ishPath + '/6'
        And request trainingPlanToUpdate
        When method POST
        Then status 204

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ == [{"moduleTitle":"Manage personal career goals","assessmentIds":[],"temporarySessionIds":[],"moduleName":"AUM1001A","moduleId":3,"sessionIds":[43],"temporaryAssessmentIds":[]}]

#       <--->  Scenario have been finished. Now change back object to default:
        Given path ishPath + '/6'
        And request trainingPlanToDefault
        When method POST
        Then status 204



    Scenario: (+) Update CourseClass tutor attendance by notadmin with access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsEdit'}

        
#       <--->

        * def trainingPlanToUpdate = [{"moduleId":3,"moduleName":"AUM1001A","sessionIds":[43],"temporarySessionIds":[],"assessmentIds":[],"temporaryAssessmentIds":[]}]

        Given path ishPath + '/6'
        And request trainingPlanToUpdate
        When method POST
        Then status 204

        Given path ishPath + '/6'
        When method GET
        Then status 200
        And match $ == [{"moduleTitle":"Manage personal career goals","assessmentIds":[],"temporarySessionIds":[],"moduleName":"AUM1001A","moduleId":3,"sessionIds":[43],"temporaryAssessmentIds":[]}]

#       <--->  Scenario have been finished. Now change back object to default:
        Given path ishPath + '/6'
        And request trainingPlanToDefault
        When method POST
        Then status 204



    Scenario: (-) Update CourseClass tutor attendance by notadmin without access rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        * def trainingPlanToUpdate = [{"moduleId":3,"moduleName":"AUM1001A","sessionIds":[43],"temporarySessionIds":[],"assessmentIds":[],"temporaryAssessmentIds":[]}]

        Given path ishPath + '/6'
        And request trainingPlanToUpdate
        When method POST
        Then status 403
        And match $.errorMessage == "Sorry, you have no permissions to edit training plans. Please contact your administrator"



    Scenario: (-) Update not existing CourseClass tutor attendance

        * def trainingPlanToUpdate = [{"moduleId":3,"moduleName":"AUM1001A","sessionIds":[43],"temporarySessionIds":[],"assessmentIds":[],"temporaryAssessmentIds":[]}]

        Given path ishPath + '/99999'
        And request trainingPlanToUpdate
        When method POST
        Then status 400
        And match $.errorMessage == "Record with id = '99999' doesn't exist."
