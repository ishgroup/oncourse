@parallel=false
Feature: Main feature for all GET requests with path 'list/entity/script' without license

    Background: Authorize first
        * configure headers = { Authorization: 'admin' }
        * url 'https://127.0.0.1:8182/a/v1'
        * def ishPath = 'list/entity/script'
        * def ishPathList = 'list'
        * def ishPathLogin = 'login'
        
  
        
    Scenario: (+) Get list of all scripts by admin

        Given path ishPathList
        And param entity = 'Script'
        And request {}
        When method POST
        Then status 200
        And assert response.rows.length >= 46


    Scenario: (+) Get existing script by admin

        Given path ishPathList
        And param entity = 'Script'
        And request {pageSize: 1000}
        When method POST
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'VET Course completion survey')]
        * def id = row.id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response.name == "VET Course completion survey"


    Scenario: (-) Get NONexisting script

        Given path ishPath + '/111111'
        When method GET
        Then status 400
        And match response.errorMessage == "Record with id = '111111' doesn't exist."

    Scenario: (+) Get list of all scripts by notadmin with rights

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsView'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Script'
        And request {}
        When method POST
        Then status 200
        And assert response.rows.length >= 46


    Scenario: (+) Get list of all scripts by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Script'
        And request {}
        When method POST
        Then status 200
        And assert response.rows.length >= 46


    Scenario: (+) Get existing script by notadmin

#       <--->  Login as notadmin
        * configure headers = { Authorization:  'UserWithRightsHide'}

        
#       <--->

        Given path ishPathList
        And param entity = 'Script'
        And request {search: 'name == "send weekly finance summary report"'}
        When method POST
        Then status 200
        * def id = response.rows[0].id

        Given path ishPath + '/' + id
        When method GET
        Then status 200
        And match response.name == 'send weekly finance summary report'


    Scenario: (+) Get existing script configs by admin

        Given path ishPathList
        And param entity = 'Script'
        And param pageSize = 1000
        When method GET
        Then status 200
        * def row = get[0] response.rows[?(@.values[0] == 'VET Course completion survey')]
        * def id = row.id

        Given path ishPath + '/config/' + id
        When method GET
        Then status 200
        And match $ contains 'shortDescription: Send the completion survey defined in vetCourseSurveyTemplate to\n  all VET students who have recently passed the timing threshold for the release of\n  the survey. You can alter the timing of this release within your survey data collection\n  forms.\ndescription: Send the completion survey defined in vetCourseSurveyTemplate to all\n  VET students who have recently passed the timing threshold for the release of the\n  survey. You can alter the timing of this release within your survey data collection\n  forms.\ncategory: Marketing\nname: VET Course completion survey\noptions:\n- name: vetCourseSurveyTemplate\n  value: ish.email.VETCourseCompletionSurvey\n  dataType: MESSAGE_TEMPLATE\ntriggerType: CRON'